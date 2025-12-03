package com.org.server.websocket.service;


import com.org.server.websocket.domain.EventEnvelope;
import com.org.server.websocket.domain.WebRtcDataType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class SignalingEventHandler implements EventHandler{

    private final static String eventType="crdt.webrtc";
    private final SimpMessagingTemplate simpMessagingTemplate;
    @Override
    public boolean supports(String type) {
        if(type.equals(eventType)){
            return true;
        }
        return false;
    }
    @Override
    public void handle(EventEnvelope env, Principal principal) {
        Map<String,Object> metaData=env.meta();
        metaData.putIfAbsent("senderId",principal.getName());
        if(metaData.get("webRtcDataType").equals(WebRtcDataType.SDPOFFER)||
                metaData.get("webRtcDataType").equals(WebRtcDataType.CANDIDATEOFFER)){
            //프로젝트 주소
            simpMessagingTemplate.convertAndSend("/topic/meet/"+metaData.get("meetId"),env);
        }
        if(metaData.get("webRtcDataType").equals(WebRtcDataType.SDPANSWER)||
                metaData.get("webRtcDataType").equals(WebRtcDataType.CANDIATEANSWER)){
            //개인 주소
            simpMessagingTemplate.convertAndSend("/user/"+metaData.get("targetId"),env);
        }
    }
    /*
    * meatdata에 들어있는값.
    * webRtcDataType==>SDPOFFER,SDPANSWER,ANDIDATEOFFER,ANDIDATEANSWER
    * senderId==>서버에서 알아서 넣음.
    * targetId==>클라이언트에서 넣어줄것.-->ANSWER인 케이스에 넣을것.
    * meetId==>서버에서 알아서 넣음.
    * */
}
