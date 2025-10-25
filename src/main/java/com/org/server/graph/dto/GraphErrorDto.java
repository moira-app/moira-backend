package com.org.server.graph.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class GraphErrorDto {

    private String requestId;
    private String rootId;
    private Long projectId;
}
