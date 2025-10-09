package com.org.server.graph.dto;

import com.org.server.graph.ChangeType;

public class NodeDelDto extends NodeDto{
    private String parentId;
    public NodeDelDto(String nodeId, ChangeType changeType,String parentId) {
        super(nodeId, changeType);
        this.parentId=parentId;
    }
}
