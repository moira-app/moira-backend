package com.org.server.graph.dto;


import com.org.server.graph.GraphActionType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class NodeDelDto extends NodeDto{
    private String parentId;

    @Builder
    public NodeDelDto(String nodeId,String requestId, String rootId
            ,String parentId, Long projectId, GraphActionType graphActionType) {
        super(nodeId,projectId,graphActionType,rootId,requestId);
        this.parentId=parentId;
    }
}
