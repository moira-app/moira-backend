package com.org.server.websocket.service;


import com.org.server.exception.MoiraSocketException;
import com.org.server.graph.GraphActionType;
import com.org.server.graph.dto.*;
import com.org.server.graph.service.GraphEnvelopService;
import com.org.server.graph.service.GraphService;
import com.org.server.websocket.domain.EventEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@Slf4j
@RequiredArgsConstructor
public class CrdtEventHandler implements EventHandler{

    private final GraphService graphService;
    private final SimpMessagingTemplate messagingTemplate;
    @Override
    public boolean supports(String type) {
        return "crdt.action".equals(type);
    }
    @Override
    public void handle(EventEnvelope env, Principal principal) {

        Long projectId = (Long) env.data().get("projectId");
        GraphActionType actionType = GraphActionType.valueOf((String) env.data().get("graphActionType"));

            switch (actionType) {
                case GraphActionType.Create -> {
                    NodeCreateDto nodeCreateDto =
                            (NodeCreateDto) GraphEnvelopService.createFromEvent(env, actionType);
                    checkPassRouting(graphService.createElementNode(nodeCreateDto, projectId), nodeCreateDto);
                    messagingTemplate.convertAndSend("/topic/crdt/" +
                            projectId, nodeCreateDto);
                }
                case GraphActionType.Delete -> {
                    NodeDelDto nodeDelDto = (NodeDelDto) GraphEnvelopService.createFromEvent(env, actionType);
                    checkPassRouting(graphService.delGraphNode(nodeDelDto), nodeDelDto);
                    messagingTemplate.convertAndSend("/topic/crdt/" +
                            projectId, nodeDelDto);
                }
                case GraphActionType.Property -> {
                    PropertyChangeDto propertyChangeDto =
                            (PropertyChangeDto) GraphEnvelopService.createFromEvent(env, actionType);
                    checkPassRouting(graphService.updateProperties(propertyChangeDto), propertyChangeDto);
                    messagingTemplate.convertAndSend("/topic/crdt/" +
                            projectId, propertyChangeDto);
                }
                default -> {
                    StructureChangeDto structureChangeDto =
                            (StructureChangeDto) GraphEnvelopService.createFromEvent(env, actionType);
                    checkPassRouting(graphService.updateNodeReference(structureChangeDto), structureChangeDto);
                    messagingTemplate.convertAndSend("/topic/crdt/" +
                            projectId, structureChangeDto);
                }
            }


    }

    private void checkPassRouting(Boolean check,NodeDto nodeDto){
        if(!check){
            nodeDto.updateCheckPass();
        }
    }
}