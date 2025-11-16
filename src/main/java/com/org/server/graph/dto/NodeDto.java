package com.org.server.graph.dto;

import com.org.server.graph.GraphActionType;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public abstract class NodeDto {

    private String requestId;
    private String nodeId;
    private String rootId;
    private Long projectId;
    private GraphActionType graphActionType;

    public NodeDto(String nodeId,Long projectId
            ,GraphActionType graphActionType,String rootId,String requestId) {
        this.nodeId = nodeId;
        this.projectId=projectId;
        this.graphActionType=graphActionType;
        this.rootId=rootId;
        this.requestId=requestId;
    }
}
