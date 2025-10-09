package com.org.server.graph.dto;

import com.org.server.graph.ChangeType;
import com.org.server.graph.dto.NodeDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PropertyChangeDto extends NodeDto {

    private String name;
    private String value;
    private LocalDateTime modifyDate;


    @Builder
    public PropertyChangeDto(String nodeId, ChangeType changeType
            ,String name,String value,LocalDateTime modifyDate) {
        super(nodeId, changeType);
        this.name=name;
        this.value=value;
        this.modifyDate=modifyDate;
    }
}
