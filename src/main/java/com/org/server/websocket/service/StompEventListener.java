package com.org.server.websocket.service;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class StompEventListener {

    private final RedisStompService redisStompService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private static final String stompSessionBaseKey="BASEKEY";

    //아래는 입장 퇴장시 정보를 갱신하기위해서 만든것.
    @EventListener
    public void catchSubScribeEvent(SessionSubscribeEvent event){
        StompHeaderAccessor acc=StompHeaderAccessor.wrap(event.getMessage());
        redisStompService.addSubScribeDest(acc.getUser().getName(),acc.getDestination());

    }
    @EventListener
    public void catchUnSubScribeEvent(SessionUnsubscribeEvent event){
        StompHeaderAccessor acc=StompHeaderAccessor.wrap(event.getMessage());
        redisStompService.removeSubScribeDest(acc.getUser().getName(),acc.getDestination());

    }
    @EventListener
    public void catchDisConnectEvent(SessionDisconnectEvent event) {
        if(event.getUser()!=null) {
            List<String> subKey = redisStompService.getSubScribeAndDelKey(event.getUser().getName());
            subKey.stream().forEach(x->log.info("key:{}",x));
        }
    }

}
