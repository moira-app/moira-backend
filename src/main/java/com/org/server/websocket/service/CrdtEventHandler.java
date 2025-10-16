package com.org.server.websocket.service;


import com.org.server.graph.GraphActionType;
import com.org.server.graph.NodeType;
import com.org.server.graph.domain.Graph;
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

        GraphActionType actionType=GraphActionType.valueOf((String)env.data().get("graphActionType"));

        if(actionType.equals(GraphActionType.Create)){
            NodeCreateDto nodeCreateDto=
                    (NodeCreateDto) GraphEnvelopService.createFromEvent(env,actionType);
            graphService.createElementNode(nodeCreateDto);
            messagingTemplate.convertAndSend("/topic/crdt/"+nodeCreateDto.getProjectId(),nodeCreateDto);
        }
        if(actionType.equals(GraphActionType.Delete)){
            NodeDelDto nodeDelDto=(NodeDelDto) GraphEnvelopService.createFromEvent(env,actionType);
            graphService.delGraphNode(nodeDelDto.getNodeId());
            messagingTemplate.convertAndSend("/topic/crdt/"+nodeDelDto.getProjectId(),nodeDelDto);
        }
        if(actionType.equals(GraphActionType.Property)){
            PropertyChangeDto propertyChangeDto=
                    (PropertyChangeDto) GraphEnvelopService.createFromEvent(env,actionType);
            graphService.updateProperties(propertyChangeDto);
            messagingTemplate.convertAndSend("/topic/crdt/"+propertyChangeDto.getProjectId(),propertyChangeDto);
        }
        if(actionType.equals(GraphActionType.Structure)){
            StructureChangeDto structureChangeDto=
                    (StructureChangeDto) GraphEnvelopService.createFromEvent(env,actionType);
           graphService.updateNodeReference(structureChangeDto);
           messagingTemplate.convertAndSend("/topic/crdt/"+structureChangeDto.getProjectId(),structureChangeDto);
        }
    }
}