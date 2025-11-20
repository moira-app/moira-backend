package com.org.server.graph.service;


import com.org.server.graph.dto.GraphDto;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.SetOperation.set;
import static org.springframework.data.mongodb.core.query.Criteria.where;


@Component
public class AsyncMethod {
    @Async
    public CompletableFuture<Void> bulkDelete(String graphId, MongoTemplate mongoTemplate){
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
            return CompletableFuture.completedFuture(null);

    }
}
