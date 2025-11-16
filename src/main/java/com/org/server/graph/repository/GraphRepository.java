package com.org.server.graph.repository;

import com.org.server.graph.NodeType;
import com.org.server.graph.domain.Graph;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GraphRepository extends MongoRepository<Graph,String> {
    List<Graph> findByProjectIdAndDeletedAndNodeType(Long projectId, Boolean bools, NodeType nodeType);
}