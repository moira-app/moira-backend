package com.org.server.graph.service;


import com.mongodb.MongoBulkWriteException;
import com.mongodb.bulk.BulkWriteError;
import com.org.server.graph.domain.Graph;
import com.org.server.graph.dto.SimpleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
@RequiredArgsConstructor
@Slf4j
public class GraphAsyncService {

    private final MongoTemplate mongoTemplate;
    @Async
    public void bulkDelete(String graphId){
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
        //이렇게 쓰면 트랜잭션 내에서 전부다 다뤄진다.-->수정함 트랜잭션을 없앴으므로 bulkopertions으로 실패한건 따로 추적을시도.
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
        catch (Exception e) {
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
        }
        finally {
            log.info("비동기 노드들 삭제처리 완료");
        }
    }

}
