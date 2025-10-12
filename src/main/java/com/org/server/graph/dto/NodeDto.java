package com.org.server.graph.dto;

import com.org.server.graph.ChangeType;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public abstract class NodeDto {
    private String nodeId;
    private ChangeType changeType;

    public NodeDto(String nodeId, ChangeType changeType) {
        this.nodeId = nodeId;
        this.changeType = changeType;
    }
}
