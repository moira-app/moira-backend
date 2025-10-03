package com.org.server.graph.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class ElementCreateDto {
    private String id;
    private String parentId;
    private Map<String,Properties> propertiesList;
}
