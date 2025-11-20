package com.org.server.graph.service;

import com.mongodb.client.result.UpdateResult;
import com.org.server.graph.GraphTransaction;
import com.org.server.graph.NodeType;
import com.org.server.graph.domain.*;
import com.org.server.graph.dto.*;
import com.org.server.graph.repository.GraphRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;


import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@RequiredArgsConstructor
@Service
@Slf4j
public class GraphService {


    private final AsyncMethod asyncMethod;
    private final GraphRepository graphRepository;
    private final MongoTemplate mongoTemplate;


    public List<Graph> getRootNodes(Long projectId){
        return graphRepository.findByProjectIdAndDeletedAndNodeType(projectId,false, NodeType.ROOT);
    }

    public Map<String, List<Graph>> getWholeGraph(String rootId){
        log.info("그래프 조회 시작");
        MatchOperation matchStage = match(where("_id").is(rootId));

        Criteria filter= where("deleted").is(false);

        GraphLookupOperation graphLookupStage = graphLookup("graph")
                .startWith("$_id")
                .connectFrom("_id")
                .connectTo("parentId")
                .restrict(filter)//where 즉 필터링
                .as("descendants");

        ProjectionOperation projectStage = project("pageType", "createDate","parentId","descendants")
                .and("_id").as("startId");

        Aggregation aggregation = newAggregation(matchStage, graphLookupStage,projectStage);

        AggregationResults<GraphDto> results =mongoTemplate.aggregate(
                aggregation, "graph", GraphDto.class
        );
        Map<String,List<Graph>> domTree=new HashMap<>();

        GraphDto graphDto=results.getUniqueMappedResult();
        if(graphDto==null){
            return domTree;
        }
        List<Graph> graphs=graphDto.getDescendants();

        graphs.stream()
                .filter(x->{
                    Element p=(Element) x;
                    if(x.getNodeType().equals(NodeType.ELEMENT)&&p.getParentId()!=null){
                        return true;
                    }
                    return false;
                })
                .forEach(x->{
                        Element p=(Element) x;
                        List<Graph> data=domTree.getOrDefault(p.getParentId(),null);
                        if(data==null){
                            data=new ArrayList<>();
                            data.add(p);
                            domTree.put(p.getParentId(),data);
                        }
                        else{
                            data.add(p);
                        }
                });
        return domTree;
    }
    public Boolean createElementNode(NodeCreateDto nodeCreateDto){
        if(nodeCreateDto.getNodeType().equals(NodeType.ROOT)){
            Root root = new Root(nodeCreateDto.getNodeId(), LocalDateTime.now().toString()
                    ,nodeCreateDto.getProjectId(),nodeCreateDto.getRootName());
            graphRepository.save(root);
            return true;
        }
        if(nodeCreateDto.getNodeType().equals(NodeType.ELEMENT)){
            LocalDateTime now=LocalDateTime.now();
            Element element=
                    new Element(nodeCreateDto.getNodeId()
                            ,nodeCreateDto.getParentId(),
                            nodeCreateDto.getPropertiesList(),now.toString()
                            ,null);
            graphRepository.save(element);
            return true;
        }
        return false;
    }
    /**
     * moving id는 움직이는애, stayId는 movingId가 그아래로 들어가고자하는 id
     * 애는 redssion락을 그냥 rootid단위로 걸어야될듯 즉 a-b를 수정하는거랑 c-d를 수정하는것은 각각
     * 다른 노들을 수정하는거지만 전부다 redssion 락 영향을 받게 설계.
     * 단 아래의 속성 수정과는 공유되지 않는 락 즉 속성 수정이 트리구조 수정의 영향을 받지는 않으니까.
     */
    @GraphTransaction
    public Boolean updateNodeReference(StructureChangeDto structureChangeDto){
        if(structureChangeDto.getNodeId().equals(structureChangeDto.getParentId())){
            return false;
        }
        Optional<Graph> movingNode= graphRepository.findById(structureChangeDto.getNodeId());
        Optional<Graph> stayNode= graphRepository.findById(structureChangeDto.getParentId());
        if(movingNode.isEmpty()||stayNode.isEmpty()||movingNode.get().getDeleted()||stayNode
                .get().getDeleted()||movingNode.get().getNodeType().equals(NodeType.ROOT)){
            return false;
        }
        if(checkCycleExist(movingNode.get().getId(),stayNode.get().getId())){
            return false;
        }
        Query query=new Query(where("_id").is(movingNode.get().getId()));
        Update updateData=new Update().set("parentId",stayNode.get().getId());
        UpdateResult result =mongoTemplate.updateFirst(query,updateData,Element.class);
        if(result.getModifiedCount()==0){
            return false;
        }
        return true;
    }

    /**
     * 애는 해당되는 노드의 속성값을 기준으로 락 즉 a노드의 b,c 2가지 속성이 존재시
     * 각각 a-b 락,a-c락을 걸자--> 이유는 한객체의 각기다른 속성 수정시엔 락이 걸릴 이유가없기떄문.
     * 참고로 각 문서의 다른 프로퍼티 수정은 서로 충돌치않음.
     * graph 트랜잭션을 구현한 이유는 1객체의 속성들을 수정하려고할때 각자 다른 속성들간에는
     * 충돌없이 수정하게하고 , 같은 속성을 수정하려는 시도는 각각 순서를 지키면서 해당 속성값의 수정시각과
     * 비교해서 수정해야될지 말아야 할지를 따지기 위함이다.
     * */

    public Boolean updateProperties(PropertyChangeDto propertyChangeDto){

        Query query=new Query(where("_id").is(propertyChangeDto.getNodeId())
                .and("deleted").is(false)
                .and("properties."+propertyChangeDto.getName()+".modifyDate")
                .lt(propertyChangeDto.getModifyDate()));
        Update update = new Update();
        update.set("properties."+propertyChangeDto.getName()+".value",propertyChangeDto.getValue());
        update.set("properties."+propertyChangeDto.getName()+".modifyDate",propertyChangeDto.getModifyDate());

        UpdateResult result= mongoTemplate.updateFirst(query, update, Element.class);
        if(result.getModifiedCount()==0){
            return false;
        }
        return true;
    }

    public Boolean delGraphNode(NodeDelDto nodeDelDto){
        Query query=new Query(where("_id").is(nodeDelDto.getNodeId())
                .and("deleted").is(false));
        Update update = new Update();
        update.set("deleted",true);
        UpdateResult result= mongoTemplate.updateFirst(query, update, Element.class);
        if(result.getModifiedCount()==0){
            return false;
        }
        CompletableFuture<Void> future=bulkDelete(nodeDelDto.getNodeId());
        future.thenAccept(x->{
            log.info("트리구조 bulkdel 성공:{}\n",nodeDelDto.getNodeId());
        }).exceptionally(ex->{
            log.info("트리구조 bulkdel 중 에러발생:{}-{}\n",nodeDelDto.getNodeId(),ex.getCause().getClass());
            //나중에 메시지큐로 넘겨서 에러코드를 다시 실행하게 만드는게 들어갈곳.
            return null;
        });
        log.info("삭제 완료\n");
        return true;
    }

    public CompletableFuture<Void> bulkDelete(String nodeId){
        try {
            return asyncMethod.bulkDelete(nodeId, mongoTemplate);
        }
        //RejectedExecutionException에러는 runasync에서 fail하는 future로 주지않고 그냥 에러로 호출한 함수로 던져버림
        //그래서 future로 넘기고싶으면 아래와같이 catch문으로 에러를 잡야아한다.
        //즉 애초에 future자체를 실행할수없는 환경이라서 fail상태로도 못만든다고 보면된다.
        catch (RejectedExecutionException e){
            return CompletableFuture.failedFuture(e);
        }
    }

    private boolean checkCycleExist(String movingId,String stayId){
        log.info("cycle start");
        MatchOperation matchStage = match(where("_id").is(movingId));

        Criteria filter= where("deleted").is(false);

        GraphLookupOperation graphLookupStage = graphLookup("graph")
                .startWith("$_id")
                .connectFrom("_id")
                .connectTo("parentId")
                .restrict(filter)
                .as("descendants");

        MatchOperation checkForStayIdStage = match(where("descendants._id").is(stayId));

        ProjectionOperation projectStage = project("nodeType",
                "createDate","parentId","descendants")
                .and("_id").as("startId");


        Aggregation aggregation = newAggregation(matchStage,
                graphLookupStage,checkForStayIdStage,projectStage);


        AggregationResults<GraphDto> results = mongoTemplate.aggregate(
                aggregation, "graph", GraphDto.class
        );
        GraphDto graphDto=results.getUniqueMappedResult();
        log.info("cycle end");
        if(graphDto!=null){
            return true;
        }
        return false;

    }

}