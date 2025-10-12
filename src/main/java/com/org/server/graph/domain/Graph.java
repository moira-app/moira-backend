package com.org.server.graph.domain;

import com.org.server.graph.NodeType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "graph")
@Getter
@NoArgsConstructor
public abstract class Graph {

    @Id
    private String id;
    private NodeType nodeType;
    private String createDate;
    private Boolean deleted=false;
    private Long projectId;


    public Graph(String id,NodeType nodeType, String createDate,Long projectId) {
        this.id=id;
        this.nodeType =nodeType;
        this.createDate=createDate;
        this.projectId=projectId;
    }
    public void updateDel(){
        this.deleted=!this.deleted;
    }
}