package com.org.server.graph.service;

import com.org.server.exception.MoiraException;
import com.org.server.graph.GraphTransaction;
import com.org.server.graph.NodeType;
import com.org.server.graph.domain.*;
import com.org.server.graph.domain.Properties;
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



    public void testing(){
        Root root=new Root("id",LocalDateTime.now().toString(),1L,"testRoot");
        graphRepository.save(root);
    }

    public List<Graph> getRootNodes(Long projectId){
        return graphRepository.findByProjectIdAndDeletedAndNodeType(projectId,false, NodeType.ROOT);
    }

    public Map<String, List<Graph>> getWholeGraph(String rootId){

        //match는 일치하는 애를 찾는것 즉 _id 컬럼이 rootid인 애를 찾아와라.
        MatchOperation matchStage = match(where("_id").is(rootId));


        //이건 추후에 필터링으로 쓰이는것. 즉 where에 해당
        Criteria filter= where("deleted").is(false);

        //$graphLookup: 재귀적 후손 탐색 메서드이다.
        GraphLookupOperation graphLookupStage = graphLookup("graph") // graph라는 컬랙션(테이블)에서
                .startWith("$_id")               // startwith-->현재 문서의 _id값으로 시작 즉 위의 matchStage에서 찾은 애의 id값을 기준으로 시작
                .connectFrom("_id")              // connectFromField--> 다른 문서와 연결할때 쓰는 필드 즉 기준점의 어떤 속성으로 다른 문서와 연결지을것이냐
                .connectTo("parentId")             // connectToField--> 연결 될 문서의 필드값 즉 a의 후손 b를 찾을떄 a의 id값 과 일치할 b의 parentid컬럼을 참조한다.
                .restrict(filter)//where 즉 필터링
                .as("descendants");              // 그 결과물을 descendants라는 배열로 담음
        //단 배열에는 mongo db내부에 저장된 형태로 들어간다. 즉 graph dto같은 프로잭션이 아니라 그냥 db에 저장된값이 담김


        //애는 select인대 결과물에서 어떤 컬럼을 선택할지, 컬럼의 이름을 뭘로 정할지를 말함.
        ProjectionOperation projectStage = project("pageType", "createDate","parentId","descendants")
                .and("_id").as("startId");

        // Aggregation Pipeline 생성 이게 뭐냐면 위에서 정의했던 연산들을 stream 처럼 쭈루룩 실행하는것이다.
        // 아래정의 된것에 의하면은 matchstage로 문서 1개를 택하고
        //그 처음 택한 문서의 id값을 시작점으로 다른애들의 parentid값과 일치하는지를 체크해서 재귀적으로 탐색하면서
        // 그 결과물이 나오는대
        // 거기서 project에 있는 항목들만 선택하겠다 이말이다.
        //참고로 graph look up stage 의 결과물은 처음 시작점이된 문서의 내부에 descendatns라는 필드가 들어가있는꼴
        /*
         * {
         *   ~~ 대충 원래 문서의 구성요소
         *   descendants: [검색한값]
         * }이런꼴이다.
         *
         * */
        //또한 당연하게도 저기 넣을 연산 순서들은 지켜줘야 올바른결과가 나온다.
        Aggregation aggregation = newAggregation(matchStage, graphLookupStage,projectStage);



        //
        // 실행을 하고 project 한결과를 graphdto에 매핑 해준다.
        //참고로 매핑시 매핑 객체의 필드명은 위에서 project에서 기재 해둔 이름과 같아야됨.
        AggregationResults<GraphDto> results = mongoTemplate.aggregate(
                aggregation, "graph", GraphDto.class
        );

        Map<String,List<Graph>> domTree=new HashMap<>();
        // 결과 가져오기 인대 만약 값이 없다면은 그냥 null을 돌려줌 즉 처음 시작 기준이 되는 문서가없거나
        //혹은 descendants의 결과물이 없다면은 그냥 null을 준다.
        GraphDto graphDto=results.getUniqueMappedResult();
        if(graphDto==null){
            return domTree;
        }
        List<Graph> graphs=graphDto.getDescendants();

        graphs.stream()
                .forEach(x->{
                    if(x.getNodeType().equals(NodeType.ELEMENT)){
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
                    }
                });
        return domTree;
    }
    public void createRootNode(Long projectId,String rootName,String id){
        Root root = new Root(id, LocalDateTime.now().toString(),projectId,rootName);
        root = graphRepository.save(root);
    }
    public void createElementNode(ElementCreateDto elementCreateDto){
        LocalDateTime now=LocalDateTime.now();
        Element element=
                new Element(elementCreateDto.getId()
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
    public void updateNodeReference(String movingId,String stayId){
        Optional<Graph> movingNode= graphRepository.findById(movingId);
        Optional<Graph> stayNode= graphRepository.findById(stayId);
        if(movingNode.isEmpty()||stayNode.isEmpty()||movingNode.get().getDeleted()||stayNode
                .get().getDeleted()){
            throw new MoiraException("없는 객체입니다", HttpStatus.BAD_REQUEST);
        }
        if(movingNode.get().getNodeType().equals(NodeType.ROOT)){
            throw new MoiraException("불가능한 요청입니다", HttpStatus.BAD_REQUEST);
        }
        if(checkCycleExist(movingId,stayId)){
            throw new MoiraException("순환고리를 만들순없습니다.", HttpStatus.BAD_REQUEST);
        }
        Query query=new Query(where("_id").is(movingId));
        Update updateData=new Update().set("parentId",stayId);
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
    public void updateProperties(PropertiesUpdateDto propertiesUpdateDto){
        Optional<Graph> g= graphRepository.findById(propertiesUpdateDto.getNodeId());
        if(g.isEmpty()||g.get().getDeleted()){
            throw new MoiraException("없는 객체입니다", HttpStatus.BAD_REQUEST);
        }
        Element e=(Element) g.get();
        Properties properties= e.getProperties().getOrDefault(propertiesUpdateDto.getName(),
                null);
        if(properties==null){
            throw new MoiraException("없는 속성입니다", HttpStatus.BAD_REQUEST);
        }



        if(propertiesUpdateDto.getModifyDate().isBefore(
                LocalDateTime.parse(properties.getModifyDate()))
        ||propertiesUpdateDto.getModifyDate().isEqual(
                LocalDateTime.parse(properties.getModifyDate()))){
            throw new MoiraException("업데이트를 할수없습니다",HttpStatus.BAD_REQUEST);
        }

        properties.updateValue(propertiesUpdateDto.getValue());
        properties.updateModifyDate(propertiesUpdateDto.getModifyDate().toString());

        Query query=new Query(where("_id").is(propertiesUpdateDto.getNodeId()));
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

        GraphLookupOperation graphLookupStage = graphLookup("graph") // from: "pages" 컬렉션
                .startWith("$_id")               // startWith: 현재 문서의 _id로 시작
                .connectFrom("_id")              // connectFromField: 다른 문서와 연결할 필드
                .connectTo("parentId")             // connectToField: 연결될 다른 문서의 필드
                .restrict(filter)
                .as("descendants");              // as: 결과를 담을 배열 필드 이름


        //이게 뭐냐면은 graphlookup으로 생성된 descendatns 값들에 대해서
        // descendatns에 들은 요소 갯수만큼 graphlookup을 복제 해주는 역할이다.
    /* [
        {
            "_id": "B",
                "name": "폴더 B",
                "parentId": "A",
                "descendants": [
                        { "_id": "C", "name": "파일 C", "parentId": "B" },
                        { "_id": "D", "name": "파일 D", "parentId": "B" }]
                        }
        ]
        를
        [{
            "_id": "B",
            "name": "폴더 B",
              "parentId": "A",
            "descendants": { "_id": "C", "name": "파일 C", "parentId": "B" }
        },
        {
            "_id": "B",
            "name": "폴더 B",
            "parentId": "A",
            "descendants": { "_id": "D", "name": "파일 D", "parentId": "B" }
        }
        ]이렇게 분해하는 연산이다.
        */
        UnwindOperation unwindStage = unwind("descendants");

        //  이건 descendatns안에있는 _id값만 select 하겠다.
        ProjectionOperation projectDescendantsStage = project()
                .and("descendants._id").as("_id");

        // 새로 집계함수 한개를 만든건대 귀찬으니까 match랑 proejct로 간략하게쓴것.
        Aggregation unionPipeline = newAggregation(
                match(where("_id").is(graphId)),
                project("_id")
        );

        //이거는 이제 union 이전에 행해진 결과물과 pipelin(~~)내부의 결과물을 합친다는말.
        UnionWithOperation unionWithStage = UnionWithOperation.unionWith("graph")
                .pipeline(unionPipeline.getPipeline());


        // 데이터들의 특정값을 true로 세팅한다.
        // 즉 [
        //  { "_id": "후손_ID_1" },
        //  { "_id": "후손_ID_2" },
        //  { "_id": "시작노드_graphId" },
        //  ...
        //] 에서 [
        //  { "_id": "후손_ID_1", "deleted": true },
        //  { "_id": "후손_ID_2", "deleted": true },
        //  { "_id": "시작노드_graphId", "deleted": true },
        //  ...
        //] 이게되개 한다
        SetOperation setStage = set("deleted").toValue(true);

        // 변경된 내용의 병합
        // 즉 graph 컬랙션에서 on으로 쓰인 값과 일치 되는애들에다가 set 과정을 지난 데이터를 합친다.
        //만약 delted가 없다면 추가하고 있으면 수정하는 방식.
        MergeOperation mergeStage = merge()
                .into(MergeOperation.MergeOperationTarget.collection("graph"))
                .on("_id")
                .whenMatched(MergeOperation.WhenDocumentsMatch.mergeDocuments())
                .build();

        // --- 전체 집계 파이프라인 조합 ---
        Aggregation aggregation = newAggregation(
                matchStage,
                graphLookupStage,
                unwindStage,
                projectDescendantsStage,
                unionWithStage,
                setStage,
                mergeStage
        );

        // 집계 실행
        // $merge는 데이터를 반환하는 대신 컬렉션에 쓰는 작업을 하므로,
        // 결과 타입은 크게 중요하지 않습니다. Document.class를 주로 사용합니다.
        AggregationResults<GraphDto> results = mongoTemplate.aggregate(
                aggregation, "graph", GraphDto.class
        );
    }
    private boolean checkCycleExist(String movingId,String stayId){
        MatchOperation matchStage = match(where("_id").is(movingId));

        Criteria filter= where("deleted").is(false);

        GraphLookupOperation graphLookupStage = graphLookup("graph") // from: "pages" 컬렉션
                .startWith("$_id")               // startWith: 현재 문서의 _id로 시작
                .connectFrom("_id")              // connectFromField: 다른 문서와 연결할 필드
                .connectTo("parentId")             // connectToField: 연결될 다른 문서의 필드
                .restrict(filter)
                .as("descendants");              // as: 결과를 담을 배열 필드 이름

        //descendants에 있는 데이터들중에서 stayid와 일치하는게 하나라도있는가 즉 or조건.
        MatchOperation checkForStayIdStage = match(where("descendants._id").is(stayId));

        ProjectionOperation projectStage = project("nodeType",
                "createDate","parentId","descendants")
                .and("_id").as("startId");


        // 이거 파이프라인 순서도 지켜줘야됨 ㅇㅇ;
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