package com.org.server.graph.service;

import com.org.server.exception.MoiraException;
import com.org.server.graph.GraphTransaction;
import com.org.server.graph.NodeType;
import com.org.server.graph.domain.*;
import com.org.server.graph.domain.Properties;
import com.org.server.graph.dto.NodeCreateDto;
import com.org.server.graph.dto.GraphDto;
import com.org.server.graph.dto.PropertyChangeDto;
import com.org.server.graph.dto.StructureChangeDto;
import com.org.server.graph.repository.GraphRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.SetOperation.set;
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

        AggregationResults<GraphDto> results = mongoTemplate.aggregate(
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
    public void createRootNode(NodeCreateDto nodeCreateDto){
        if(nodeCreateDto.getNodeType()!=NodeType.ROOT){
            throw new MoiraException("틀린 값이비다",HttpStatus.BAD_REQUEST);
        }
        Root root = new Root(nodeCreateDto.getNodeId(), LocalDateTime.now().toString()
                ,nodeCreateDto.getProjectId(),nodeCreateDto.getRootName());
        root = graphRepository.save(root);
    }
    public void createElementNode(NodeCreateDto elementCreateDto){
        if(elementCreateDto.getNodeType()!=NodeType.ELEMENT){
            throw new MoiraException("틀린 값이비다",HttpStatus.BAD_REQUEST);
        }
        LocalDateTime now=LocalDateTime.now();
        Element element=
                new Element(elementCreateDto.getNodeId()
                        ,elementCreateDto.getParentId(),elementCreateDto.getPropertiesList(),now.toString()
                        ,null);
        graphRepository.save(element);
    }
    /**
     * moving id는 움직이는애, stayId는 movingId가 그아래로 들어가고자하는 id
     * 애는 redssion락을 그냥 rootid단위로 걸어야될듯 즉 a-b를 수정하는거랑 c-d를 수정하는것은 각각
     * 다른 노들을 수정하는거지만 전부다 redssion 락 영향을 받게 설계.
     * 단 아래의 속성 수정과는 공유되지 않는 락 즉 속성 수정이 트리구조 수정의 영향을 받지는 않으니까.
     */
    @GraphTransaction
    public void updateNodeReference(StructureChangeDto structureChangeDto){
        if(structureChangeDto.getNodeId().equals(structureChangeDto.getParentId())){
            throw new MoiraException("불가능한 요청입니다", HttpStatus.BAD_REQUEST);
        }
        Optional<Graph> movingNode= graphRepository.findById(structureChangeDto.getNodeId());
        Optional<Graph> stayNode= graphRepository.findById(structureChangeDto.getParentId());
        if(movingNode.isEmpty()||stayNode.isEmpty()||movingNode.get().getDeleted()||stayNode
                .get().getDeleted()){
            throw new MoiraException("없는 객체입니다", HttpStatus.BAD_REQUEST);
        }
        if(movingNode.get().getNodeType().equals(NodeType.ROOT)){
            throw new MoiraException("불가능한 요청입니다", HttpStatus.BAD_REQUEST);
        }
        if(checkCycleExist(movingNode.get().getId(),stayNode.get().getId())){
            throw new MoiraException("순환고리를 만들순없습니다.", HttpStatus.BAD_REQUEST);
        }
        Query query=new Query(where("_id").is(movingNode.get().getId()));
        Update updateData=new Update().set("parentId",stayNode.get().getId());
        mongoTemplate.updateFirst(query,updateData,Element.class);
    }

    /**
     * 애는 해당되는 노드의 속성값을 기준으로 락 즉 a노드의 b,c 2가지 속성이 존재시
     * 각각 a-b 락,a-c락을 걸자--> 이유는 한객체의 각기다른 속성 수정시엔 락이 걸릴 이유가없기떄문.
     * 참고로 각 문서의 다른 프로퍼티 수정은 서로 충돌치않음.
     * graph 트랜잭션을 구현한 이유는 1객체의 속성들을 수정하려고할때 각자 다른 속성들간에는
     * 충돌없이 수정하게하고 , 같은 속성을 수정하려는 시도는 각각 순서를 지키면서 해당 속성값의 수정시각과
     * 비교해서 수정해야될지 말아야 할지를 따지기 위함이다.
     * */
    @GraphTransaction
    public void updateProperties(PropertyChangeDto propertyChangeDto){
        Optional<Graph> g= graphRepository.findById(propertyChangeDto.getNodeId());
        if(g.isEmpty()||g.get().getDeleted()){
            throw new MoiraException("없는 객체입니다", HttpStatus.BAD_REQUEST);
        }
        Element e=(Element) g.get();
        Properties properties= e.getProperties().getOrDefault(propertyChangeDto.getName(),
                null);
        if(properties==null){
            throw new MoiraException("없는 속성입니다", HttpStatus.BAD_REQUEST);
        }

        if(propertyChangeDto.getModifyDate().isBefore(
                LocalDateTime.parse(properties.getModifyDate()))
        ||propertyChangeDto.getModifyDate().isEqual(
                LocalDateTime.parse(properties.getModifyDate()))){
            throw new MoiraException("업데이트를 할수없습니다",HttpStatus.BAD_REQUEST);
        }
        properties.updateValue(propertyChangeDto.getValue());
        properties.updateModifyDate(propertyChangeDto.getModifyDate().toString());

        Query query=new Query(where("_id").is(propertyChangeDto.getNodeId()));
        Update updateData=new Update().set("properties",e.getProperties());
        mongoTemplate.updateFirst(query,updateData,Element.class);
    }

    public void delGraphNode(String graphId){
        Optional<Graph> g=graphRepository.findById(graphId);
        if(g.isEmpty()||g.get().getDeleted()){
            throw new MoiraException("없는 객체입니다", HttpStatus.BAD_REQUEST);
        }
        deleteBulkUpdate(graphId);
    }


    private void deleteBulkUpdate(String graphId){

        MatchOperation matchStage = match(where("_id").is(graphId));

        Criteria filter= where("deleted").is(false);

        GraphLookupOperation graphLookupStage = graphLookup("graph")
                .startWith("$_id")
                .connectFrom("_id")
                .connectTo("parentId")
                .restrict(filter)
                .as("descendants");



        UnwindOperation unwindStage = unwind("descendants");

        ProjectionOperation projectDescendantsStage = project()
                .and("descendants._id").as("_id");

        Aggregation unionPipeline = newAggregation(
                match(where("_id").is(graphId)),
                project("_id")
        );


        UnionWithOperation unionWithStage = UnionWithOperation.unionWith("graph")
                .pipeline(unionPipeline.getPipeline());



        SetOperation setStage = set("deleted").toValue(true);


        MergeOperation mergeStage = merge()
                .into(MergeOperation.MergeOperationTarget.collection("graph"))
                .on("_id")
                .whenMatched(MergeOperation.WhenDocumentsMatch.mergeDocuments())
                .build();

        Aggregation aggregation = newAggregation(
                matchStage,
                graphLookupStage,
                unwindStage,
                projectDescendantsStage,
                unionWithStage,
                setStage,
                mergeStage
        );

        AggregationResults<GraphDto> results = mongoTemplate.aggregate(
                aggregation, "graph", GraphDto.class
        );
    }
    private boolean checkCycleExist(String movingId,String stayId){
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
        if(graphDto!=null){
            return true;
        }
        return false;

    }
}