package com.org.server.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class StompErrorHandler extends StompSubProtocolErrorHandler {

    @Override
    public Message<byte []> handleClientMessageProcessingError(Message<byte []> clientMessage, Throwable ex) {

        StompHeaderAccessor headerAccessor=StompHeaderAccessor.create(StompCommand.ERROR);
        headerAccessor.setMessage(ex.getCause().getMessage());
        headerAccessor.setLeaveMutable(true);
        return MessageBuilder.createMessage("".getBytes(StandardCharsets.UTF_8)
                ,headerAccessor.getMessageHeaders());
        //d여기서 나가는 메시지의 stomp command가 error면 연결을 해제해버림(disconnect면 왜 해제가안되냐?)
    }
}
