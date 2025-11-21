package com.org.server.exception;


import com.org.server.graph.dto.GraphErrorDto;
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
    @MessageExceptionHandler(Exception.class)
    public void controlEx(Exception e){
        log.info("소켓 연결 알수없는 서버에러 발생:{}",e.getMessage());
    }
    @MessageExceptionHandler(MoiraSocketException.class)
    public void moiraSocketEx(MoiraSocketException moiraSocketException) {
        log.info("소켓 에러전송 발생");
        String des="/topic/crdt/"+moiraSocketException.getProjectId();
        simpMessagingTemplate.convertAndSend(des,moiraSocketException.getNodeDto());
    }
}
