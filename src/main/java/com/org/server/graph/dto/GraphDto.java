package com.org.server.graph.dto;

import com.org.server.graph.NodeType;
import com.org.server.graph.domain.Graph;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class GraphDto {


    @Field("_id")
    private String startId;
    private NodeType nodeType;

    private List<Graph> descendants;

    public GraphDto(String startId, NodeType nodeType,List<Graph> descendants) {
        this.startId = startId;
        this.nodeType = nodeType;
        this.descendants =descendants;
    }
}