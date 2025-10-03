package com.org.server.graph.domain;

import com.org.server.graph.NodeType;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public class Root extends Graph {
    private String rootName;
    public Root(String id, LocalDateTime localDateTime, Long projectId, String rootName) {
        super(id, NodeType.ROOT,localDateTime,projectId);
        this.rootName=rootName;
    }
}
