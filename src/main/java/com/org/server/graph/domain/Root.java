package com.org.server.graph.domain;

import com.org.server.graph.NodeType;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@NoArgsConstructor
public class Root extends Graph {


    private String rootName;
    public Root(String id, Long projectId, String rootName) {
        super(id, NodeType.ROOT,projectId);
        this.rootName=rootName;

    }
}
