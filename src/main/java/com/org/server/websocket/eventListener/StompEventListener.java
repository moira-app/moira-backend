package com.org.server.websocket.eventListener;



import com.org.server.chat.domain.ChatType;
import com.org.server.ticket.service.TicketService;
import com.org.server.websocket.domain.AlertKey;
import com.org.server.websocket.domain.AlertMessageDto;
import com.org.server.websocket.domain.EventEnvelope;
import com.org.server.websocket.service.RedisStompService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class StompEventListener {

    private final RedisStompService redisStompService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final static String chatRoomPreFix="/topic/chatroom-";
    private final TicketService ticketService;

    @EventListener
    public void catchSubScribeEvent(SessionSubscribeEvent event){
        StompHeaderAccessor acc=StompHeaderAccessor.wrap(event.getMessage());
        if(acc.getDestination().startsWith(chatRoomPreFix)){
            String [] data=acc.getDestination().split("-");
            Long projectId=Long.parseLong(data[1]);
            ChatType chatType=ChatType.valueOf(data[2]);
            if(chatType.equals(ChatType.PROJECT)) {
                AlertMessageDto alertMessageDto=AlertMessageDto.builder()
                        .alertKey(AlertKey.MEMBERLIST)
                        .projectId(projectId)
                        .data(Map.of("memberList",ticketService.getMemberListOfProject(projectId)))
                        .build();
                simpMessagingTemplate.convertAndSend("/queue/"+acc.getUser().getName(),alertMessageDto);
            }
        }
    }

    @EventListener
    public void catchDisConnectEvent(SessionDisconnectEvent event) {
        if(event.getUser()!=null) {
            redisStompService.removeSubScribeDest(event.getUser().getName());
        }
    }
    private EventEnvelope createEnv(Map<String,Object> data) {
        return EventEnvelope.builder()
                .data(data)
                .build();
    }

}
