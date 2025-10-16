package com.org.server.exception;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class SocketExceptionController {


    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageExceptionHandler(MoiraSocketException.class)
    public void moiraSocketEx(MoiraSocketException moiraSocketException) {
        String des="/topic/crdt/"+moiraSocketException.getProjectId();
        simpMessagingTemplate.convertAndSend(des,moiraSocketException.getRequestId());
    }
}
