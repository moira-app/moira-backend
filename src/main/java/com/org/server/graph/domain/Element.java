package com.org.server.graph.domain;

import com.org.server.graph.NodeType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;


@Getter
@NoArgsConstructor
public class Element extends Graph{
    private String parentId;
    private Map<String,Properties> properties;
    public Element(String id, String parentId, Map<String,Properties>properties,
                   LocalDateTime localDateTime, Long projectId) {
        super(id, NodeType.ELEMENT,localDateTime,projectId);
        this.parentId=parentId;
        this.properties=properties;
    }
    public void updateParentId(String id){
        this.parentId=id;
    }
}
