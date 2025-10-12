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
    private LocalDateTime createDate;
    private List<Graph> descendants;

    public GraphDto(String startId, NodeType nodeType, LocalDateTime createDate, List<Graph> descendants) {
        this.startId = startId;
        this.nodeType = nodeType;
        this.createDate = createDate;
        this.descendants =descendants;
    }
}