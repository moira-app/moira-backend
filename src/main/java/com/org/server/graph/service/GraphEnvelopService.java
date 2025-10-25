package com.org.server.graph.service;

import com.org.server.exception.MoiraException;
import com.org.server.exception.MoiraSocketException;
import com.org.server.graph.GraphActionType;
import com.org.server.graph.NodeType;
import com.org.server.graph.domain.Properties;
import com.org.server.graph.dto.*;
import com.org.server.websocket.domain.EventEnvelope;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class GraphEnvelopService {
    public static NodeDto createFromEvent(EventEnvelope eventEnvelope, GraphActionType graphActionType){
        String nodeId = UUID.randomUUID().toString();
        String rootId = (String) eventEnvelope.data().get("rootId");
        String requestId = (String) eventEnvelope.data().get("requestId");
        String parentId = (String) eventEnvelope.data().get("parentId");
        Long projectId = Long.parseLong((String) eventEnvelope.data().get("projectId"));
        if(graphActionType.equals(GraphActionType.Create)) {

            NodeType nodeType = NodeType.valueOf((String) eventEnvelope.data().get("nodeType"));

            Map<String, Properties> propertiesMap =
                    (Map<String, Properties>) eventEnvelope.data().get("properties");
            String rootName = (String) eventEnvelope.data().get("rootName");
            return NodeCreateDto.builder()
                    .nodeId(nodeId)
                    .rootId(rootId)
                    .requestId(requestId)
                    .parentId(parentId)
                    .nodeType(nodeType)
                    .projectId(projectId)
                    .graphActionType(graphActionType)
                    .propertiesList(propertiesMap)
                    .rootName(rootName)
                    .build();
        }
        if(graphActionType.equals(GraphActionType.Delete)||graphActionType.equals(GraphActionType.Structure)){

            return graphActionType.equals(GraphActionType.Delete) ? NodeDelDto.builder()
                    .rootId(rootId)
                    .nodeId(nodeId)
                    .requestId(requestId)
                    .parentId(parentId)
                    .graphActionType(graphActionType)
                    .projectId(projectId)
                    .build()
                : StructureChangeDto.builder()
                    .requestId(requestId)
                    .rootId(rootId)
                    .projectId(projectId)
                    .graphActionType(graphActionType)
                    .parentId(parentId)
                    .nodeId(nodeId)
                    .build();

        }
        if(graphActionType.equals(GraphActionType.Property)){
            String value = (String) eventEnvelope.data().get("value");
            LocalDateTime modifyDate=LocalDateTime.parse((String)eventEnvelope.data().get("modifyDate"));
            String name = (String) eventEnvelope.data().get("name");
            return PropertyChangeDto.builder()
                    .graphActionType(graphActionType)
                    .rootId(rootId)
                    .requestId(requestId)
                    .projectId(projectId)
                    .nodeId(nodeId)
                    .value(value)
                    .name(name)
                    .modifyDate(modifyDate)
                    .build();
        }

        throw new MoiraSocketException("지원하지 않는 타입입니다",projectId,requestId,rootId);
    }
}
