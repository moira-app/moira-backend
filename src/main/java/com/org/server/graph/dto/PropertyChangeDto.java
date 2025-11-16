package com.org.server.graph.dto;

import com.org.server.graph.GraphActionType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PropertyChangeDto extends NodeDto {

    private String name;
    private String value;
    private LocalDateTime modifyDate;


    @Builder
    public PropertyChangeDto(String nodeId,String rootId,String requestId, String name, String value,
                             LocalDateTime modifyDate, Long projectId, GraphActionType graphActionType) {
        super(nodeId,projectId,graphActionType,rootId,requestId);
        this.name=name;
        this.value=value;
        this.modifyDate=modifyDate;
    }
}
