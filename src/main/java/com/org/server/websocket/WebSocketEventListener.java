package com.org.server.websocket;

import com.org.server.redis.service.RedisUserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

import java.security.Principal;

//@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final RedisTemplate<String,String> redisTemplate;

    @EventListener
    public void catchSessionDisconnectEvent(SessionDisconnectEvent event){
    }

    @EventListener
    public void catchSubScribeEvent(SessionSubscribeEvent event){
        StompHeaderAccessor acc=StompHeaderAccessor.wrap(event.getMessage());
        Principal principal=(Principal)acc.getSessionAttributes().get("principal");
        String dest=acc.getDestination();
        String sessionId=acc.getSessionId();
        //redisTemplate.opsForList().set

    }

    @EventListener
    public void catchConnectionEvent(SessionConnectEvent event){

    }

    @EventListener
    public void catchUnSubScribeEvent(SessionUnsubscribeEvent event){
    }

    private StompHeaderAccessor createAcc(AbstractSubProtocolEvent event){
        return StompHeaderAccessor.wrap(event.getMessage());
    }
}
