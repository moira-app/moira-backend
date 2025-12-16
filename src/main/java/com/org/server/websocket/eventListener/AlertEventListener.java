package com.org.server.websocket.eventListener;


import com.org.server.chat.domain.ChatRoom;
import com.org.server.chat.domain.ChatType;
import com.org.server.chat.service.ChatRoomService;
import com.org.server.redis.service.RedisUserInfoService;
import com.org.server.ticket.domain.TicketMetaDto;
import com.org.server.ticket.repository.AdvanceTicketRepository;
import com.org.server.ticket.repository.TicketRepository;
import com.org.server.ticket.service.TicketService;
import com.org.server.websocket.domain.AlertKey;
import com.org.server.websocket.domain.AlertMessageDto;
import com.org.server.websocket.domain.GlobalAlertMessageDto;
import com.org.server.websocket.service.RedisStompService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Set;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class AlertEventListener {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatRoomService chatRoomService;
    private final RedisStompService redisStompService;
    private final RedisUserInfoService redisUserInfoService;
    private final AdvanceTicketRepository advanceTicketRepository;
    private final static String chatRoomPreFix="/topic/chatroom-";

    //@Async
    @TransactionalEventListener(phase= AFTER_COMMIT)
    public void alertMessage(AlertMessageDto alertMessageDto){
        ChatRoom chatRoom = chatRoomService.ensureRoom(ChatType.PROJECT, alertMessageDto.projectId());
        simpMessagingTemplate.convertAndSend(
                    chatRoomPreFix + alertMessageDto.projectId() +
                            "-" + ChatType.PROJECT + "-" + chatRoom.getId(), alertMessageDto);
    }

    @TransactionalEventListener(phase= AFTER_COMMIT)
    public void alertGlobalMessage(GlobalAlertMessageDto alertMessageDto){
            List<TicketMetaDto> ticketMetaDtoList=
                    advanceTicketRepository.getProjectList(Long.parseLong(alertMessageDto.memberId()));
            ticketMetaDtoList.stream().forEach(x->{
                simpMessagingTemplate.convertAndSend(chatRoomPreFix+x.getProjectId()
                        +"-"+ChatType.PROJECT+"-"+ x.getChatRoomId(),createAlertMessageFromGlobal(alertMessageDto,x.getProjectId()) );
            });
    }

    private AlertMessageDto createAlertMessageFromGlobal(GlobalAlertMessageDto alertMessageDto,Long projectId){
        return AlertMessageDto.builder()
                .alertKey(alertMessageDto.alertKey())
                .projectId(projectId)
                .data(alertMessageDto.data())
                .build();
    }



}
