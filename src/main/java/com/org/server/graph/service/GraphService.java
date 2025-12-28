package com.org.server.graph.service;

import com.mongodb.MongoBulkWriteException;
import com.mongodb.bulk.BulkWriteError;
import com.mongodb.client.result.UpdateResult;
import com.org.server.graph.GraphTransaction;
import com.org.server.graph.NodeType;
import com.org.server.graph.domain.*;
import com.org.server.graph.dto.*;
import com.org.server.graph.repository.GraphRepository;
import com.org.server.util.DateTimeMapUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@RequiredArgsConstructor
@Service
@Slf4j
public class GraphService {

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
    public Boolean createElementNode(NodeCreateDto nodeCreateDto,Long projectId){
        if(nodeCreateDto.getNodeType().equals(NodeType.ROOT)){
            Root root = new Root(nodeCreateDto.getNodeId(), LocalDateTime.now().toString()
                    ,projectId,nodeCreateDto.getRootName());
            root=graphRepository.save(root);
            nodeCreateDto.updateNodeId(root.getId());
            return true;
        }
        if(nodeCreateDto.getNodeType().equals(NodeType.ELEMENT)){
            LocalDateTime now=LocalDateTime.now();
            Element element=
                    new Element(nodeCreateDto.getNodeId()
                            ,nodeCreateDto.getParentId(),
                            nodeCreateDto.getPropertiesList(),now.toString());
            element=graphRepository.save(element);
            nodeCreateDto.updateNodeId(element.getId());
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
        Optional<Graph> movingNode=graphRepository.findById(structureChangeDto.getNodeId());
        Optional<Graph> stayNode=graphRepository.findById(structureChangeDto.getParentId());
        if (structureChangeDto.getNodeId().equals(structureChangeDto.getParentId())
        ||movingNode.isEmpty()||stayNode.isEmpty()
                ||movingNode.get().getDeleted() ||stayNode.get().getDeleted()
                ||structureChangeDto.getNodeId().equals(structureChangeDto.getRootId())) {
                return false;
        }
        if (checkCycleExist(structureChangeDto.getNodeId(), structureChangeDto.getParentId())) {
            return false;
        }
        Query query = new Query(where("_id").is(structureChangeDto.getNodeId())
                    .and("deleted").is(false));
        Update updateData = new Update().set("parentId", structureChangeDto.getParentId());
        UpdateResult result = mongoTemplate.updateFirst(query, updateData, Element.class);
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


        LocalDateTime modifyDate=LocalDateTime.parse(propertyChangeDto.getModifyDate(),
                DateTimeMapUtil.FLEXIBLE_NANO_FORMATTER);

        Query query=new Query(where("_id").is(propertyChangeDto.getNodeId())
                .and("deleted").is(false)
                .and("properties."+propertyChangeDto.getName()+".modifyDate")
                .lt(modifyDate));
        Update update = new Update();
        update.set("properties."+propertyChangeDto.getName()+".value",propertyChangeDto.getValue());
        update.set("properties."+propertyChangeDto.getName()+".modifyDate",modifyDate);

        UpdateResult result= mongoTemplate.updateFirst(query, update, Element.class);
        if(result.getModifiedCount()==0){
            return false;
        }
        return true;
    }

    @GraphTransaction
    //@Transactional(value ="mongoTransactionManager")
    /*
    * 몽고 디비 트랜잭션의 경우 기본적으로 단일문서에 대한 트랜잭션은 지원이된다.
    * 단일문서 트랜잭션은 트랜잭션 어노테이션없이 적용되며 snapshot 단계가 아닌 한문서에 대한 update요청을 순차적으로 처리하는
    * read commited 단계이다.
    * 그러나 트랜잭션 어노테이션을 적용할경우 강제로 snaphost 단계의 isolation lv가 적용이된다.
    * 즉 repeatable read라고 생각하면되는대 이는 해당 트랜잭션동안 외부의 트랜잭션이 특정 문서를 수정하고
    * 해당 트랜잭션이 그 문서를수정하고자 할경우 snap shot과 달라서 write conflict 문제가 발생한다.
    * 본 프로젝트에선 한개의 그래프에 대한 property 수정과 삭제-트리구조 수정과정이 충돌하지않기 위해서 트랜잭션을 걸지 않도록 하고자한다.
    * 또한 삭제-트리구조 수정과정이 제어가 되지않을 경우 문제가 발생할수잇으므로 해당 과정은 redisson 분산락을 통해서 비관적락을 비슷하게
    * 구현하였다.
    * */
    public Boolean delGraphNode(NodeDelDto nodeDelDto){
            Query query = new Query(where("_id").is(nodeDelDto.getNodeId())
                    .and("deleted").is(false));
            Update update = new Update();
            update.set("deleted", true);
            UpdateResult result = mongoTemplate.updateFirst(query, update, Element.class);
            if (result.getModifiedCount() == 0) {
                return false;
            }
            bulkDelete(nodeDelDto.getNodeId());
            log.info("삭제 완료\n");
            return true;
    }
    private void bulkDelete(String graphId){
        MatchOperation matchStage = match(where("_id").is(graphId));
        Criteria filter = where("deleted").is(false);
        GraphLookupOperation graphLookupStage = graphLookup("graph")
                .startWith("$_id")
                .connectFrom("_id")
                .connectTo("parentId")
                .restrict(filter)
                .as("descendants");
        UnwindOperation unwindStage = unwind("descendants");

        ProjectionOperation projectDescendantsStage = project()
                .and("descendants._id").as("_id");

        Aggregation aggregation = newAggregation(
                matchStage,
                graphLookupStage,
                unwindStage,
                projectDescendantsStage);
        //merge,set,unionwithstage같은 복잡한애는 multi doc transaction에서 사용할수없어서 이렇게 바꿈.
        //이렇게 쓰면 트랜잭션 내에서 전부다 다뤄진다.-->수정 트랜잭션을 없앴으므로 bulkopertions으로 실패한건 따로 추적을시도.
        List<String> results = mongoTemplate.aggregate(
                        aggregation, "graph", SimpleDto.class
                ).getMappedResults()
                .stream().map(x->{
                    return x.getId();
                }).collect(Collectors.toList());

        BulkOperations bulkOperations=mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Graph.class);
        results.stream().forEach(x->{
            Query query =new Query(where("_id").in(x));
            Update update = new Update().set("deleted", true);
            bulkOperations.updateOne(query,update);
        });
        try {
            if(!results.isEmpty()){
                bulkOperations.execute();
            }
        }
        catch (DataAccessException e) {
            if (e.getCause() instanceof MongoBulkWriteException) {
                MongoBulkWriteException bulkEx = (MongoBulkWriteException) e.getCause();
                List<BulkWriteError> errors = bulkEx.getWriteErrors();
                for (BulkWriteError err : errors) {
                    System.out.println("실패한 인덱스: " + err.getIndex() + ", 메시지: " + err.getMessage());
                    //메시징 큐에 재시도 로그 남기는 영역.
                    //err.getindex는 bulkopertion에넣은 순서 즉 일반적인 list 인덱스를 의미하며 stream으로 차례대로넣었으므로
                    //results에서 꺼내와서 쓰면된다.
                }
            } else {
                System.err.println("에러 메시지: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("예상치 못한 오류: " + e.getMessage());
        }
    }

    private boolean checkCycleExist(String movingId,String stayId){
        log.info("cycle start");
        MatchOperation matchStage = match(where("_id").is(movingId).and("deleted").is(false));

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