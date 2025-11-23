package com.org.server.scheduler.scheduledTask;


import com.mongodb.client.result.DeleteResult;
import com.org.server.graph.domain.Graph;
import lombok.RequiredArgsConstructor;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

import static org.springframework.data.mongodb.core.query.Criteria.where;
@Component
@RequiredArgsConstructor
public class MongoDelScheduler implements ScheduledFuture{

    private final MongoTemplate mongoTemplate;

    @Override
    public String taskName() {
        return "mongoDelete";
    }
    @Async
    @Transactional(value ="mongoTransactionManager")
    @Override
    public CompletableFuture<String> scheduledTask() {
        try {
            Query query = new Query(where("deleted").is(true));
            DeleteResult deleteResult = mongoTemplate.remove(query, Graph.class);
            throw new RuntimeException("에러 발생");
            //return CompletableFuture.completedFuture(taskName());
        }
        catch (Exception e){

            return CompletableFuture.failedFuture(new RuntimeException(e.getMessage()+"-"+taskName()));
        }
    }
}
