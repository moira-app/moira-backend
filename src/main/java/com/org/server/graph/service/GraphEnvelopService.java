package com.org.server.graph.service;

import com.org.server.exception.MoiraException;
import com.org.server.exception.MoiraSocketException;
import com.org.server.graph.GraphActionType;
import com.org.server.graph.NodeType;
import com.org.server.graph.domain.Properties;
import com.org.server.graph.dto.*;
import com.org.server.util.DateTimeMapUtil;
import com.org.server.websocket.domain.EventEnvelope;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class GraphEnvelopService {
    public static NodeDto createFromEvent(EventEnvelope eventEnvelope, GraphActionType graphActionType){
        String nodeId = (String) eventEnvelope.data().getOrDefault("nodeId",null);
        String rootId = (String) eventEnvelope.data().getOrDefault("rootId",null);
        String requestId = (String) eventEnvelope.data().get("requestId");
        String parentId = (String) eventEnvelope.data().getOrDefault("parentId",null);

        switch (graphActionType){
            case GraphActionType.Create->{
                NodeType nodeType = NodeType.valueOf((String) eventEnvelope.data().get("nodeType"));
                Map<String, Properties> propertiesMap =
                        (Map<String, Properties>) eventEnvelope.data().getOrDefault("properties",Map.of());
                String rootName = (String) eventEnvelope.data().getOrDefault("rootName",null);
                return NodeCreateDto.builder()
                        .nodeId(nodeId)
                        .rootId(rootId)
                        .requestId(requestId)
                        .parentId(parentId)
                        .nodeType(nodeType)
                        .graphActionType(graphActionType)
                        .propertiesList(propertiesMap)
                        .rootName(rootName)
                        .build();
            }
            case GraphActionType.Delete -> {
                return NodeDelDto.builder()
                        .rootId(rootId)
                        .nodeId(nodeId)
                        .requestId(requestId)
                        .parentId(parentId)
                        .graphActionType(graphActionType)
                        .build();
            }
            case GraphActionType.Structure -> {
                return StructureChangeDto.builder()
                        .requestId(requestId)
                        .rootId(rootId)
                        .graphActionType(graphActionType)
                        .parentId(parentId)
                        .nodeId(nodeId)
                        .build();
            }
            case Property -> {
                String value = (String) eventEnvelope.data().get("value");
                String modifyDate=(String)eventEnvelope.data().get("modifyDate");
                String name = (String) eventEnvelope.data().get("name");
                return PropertyChangeDto.builder()
                        .graphActionType(graphActionType)
                        .rootId(rootId)
                        .requestId(requestId)
                        .nodeId(nodeId)
                        .value(value)
                        .name(name)
                        .modifyDate(modifyDate)
                        .build();
            }
            default -> {
                Long projectId = Long.parseLong((String) eventEnvelope.data().get("projectId"));
                throw new MoiraSocketException("지원하지 않는 타입입니다",projectId,NodeCreateDto.builder()
                        .nodeId(nodeId)
                        .rootId(rootId)
                        .requestId(requestId)
                        .build());
            }
        }

    }
}
