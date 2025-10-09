package com.org.server.graph.dto;

import com.org.server.graph.ChangeType;
import lombok.Builder;
import lombok.Getter;


@Getter
public class StructureChangeDto extends NodeDto {
    private String parentId;

    @Builder
    public StructureChangeDto(String nodeId, ChangeType changeType, String parentId) {
        super(nodeId, changeType);
        this.parentId=parentId;
    }
}
