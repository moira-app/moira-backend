package com.org.server.graph.dto;


import com.org.server.graph.GraphActionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class NodeDelDto extends NodeDto{
    private String parentId;

    @Builder
    public NodeDelDto(String nodeId,String requestId, String rootId
            ,String parentId,  GraphActionType graphActionType) {
        super(nodeId,graphActionType,rootId,requestId);
        this.parentId=parentId;
    }
}
