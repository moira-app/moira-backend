package com.org.server.graph.dto;

import com.org.server.graph.GraphActionType;
import com.org.server.graph.NodeType;
import com.org.server.graph.domain.Properties;
import com.org.server.websocket.domain.EventEnvelope;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;


@Getter
@NoArgsConstructor
public class NodeCreateDto extends NodeDto {

    private NodeType nodeType;
    private String parentId;
    private String rootName;
    private Map<String, Properties> propertiesList;


    @Builder
    public NodeCreateDto(String nodeId,String parentId,String requestId,String rootId,
                         Map<String,Properties> propertiesList,
                         NodeType nodeType,String rootName,Long projectId,GraphActionType graphActionType) {
        super(nodeId,projectId,graphActionType,rootId,requestId);
        this.parentId=parentId;
        this.propertiesList=propertiesList;
        this.nodeType=nodeType;
        this.rootName=rootName;

    }
}
