package com.org.server.graph.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor

public class PropertiesUpdateDto {
    private String nodeId;
    private LocalDateTime modifyDate;
    private String name;
    private String value;

    @Builder

    public PropertiesUpdateDto(String nodeId, LocalDateTime modifyDate, String name, String value) {
        this.nodeId = nodeId;
        this.modifyDate = modifyDate;
        this.name = name;
        this.value = value;
    }
}
