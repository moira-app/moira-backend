package com.org.server.websocket.eventListener;


import com.org.server.chat.domain.ChatRoom;
import com.org.server.chat.domain.ChatType;
import com.org.server.chat.service.ChatRoomService;
import com.org.server.websocket.domain.AlertMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class AlertEventListener {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatRoomService chatRoomService;
    private final static String chatRoomPreFix="/topic/chatroom-";

    //@Async
    @TransactionalEventListener(phase= AFTER_COMMIT)
    public void alertMessage(AlertMessageDto alertMessageDto){


        ChatRoom chatRoom=chatRoomService.ensureRoom(ChatType.PROJECT,alertMessageDto.projectId());
        simpMessagingTemplate.convertAndSend(
                chatRoomPreFix+alertMessageDto.projectId()+
                        "-"+ChatType.PROJECT +"-"+chatRoom.getId(),alertMessageDto);

    }
}
