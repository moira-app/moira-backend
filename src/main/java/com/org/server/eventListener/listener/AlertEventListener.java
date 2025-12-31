package com.org.server.eventListener.listener;


import com.org.server.chat.domain.ChatRoom;
import com.org.server.chat.domain.ChatType;
import com.org.server.chat.service.ChatRoomService;
import com.org.server.redis.service.RedisIntegralService;
import com.org.server.ticket.domain.TicketMetaDto;
import com.org.server.ticket.repository.AdvanceTicketRepository;
import com.org.server.eventListener.domain.AlertMessageDto;
import com.org.server.eventListener.domain.MemberAlertMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class AlertEventListener {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatRoomService chatRoomService;
    private final AdvanceTicketRepository advanceTicketRepository;
    private final RedisIntegralService redisIntegralService;
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
    public void alertGlobalMessage(MemberAlertMessageDto alertMessageDto){
            List<TicketMetaDto> ticketMetaDtoList=
                    advanceTicketRepository.getProjectList(Long.parseLong(alertMessageDto.memberId()));
            ticketMetaDtoList.stream().forEach(x->{
                simpMessagingTemplate.convertAndSend(chatRoomPreFix+x.getProjectId()
                        +"-"+ChatType.PROJECT+"-"+ x.getChatRoomId(),createAlertMessageFromGlobal(alertMessageDto,x.getProjectId()) );
            });
            redisIntegralService.integralDelMemberInfo(alertMessageDto.memberId(),ticketMetaDtoList);
    }

    private AlertMessageDto createAlertMessageFromGlobal(MemberAlertMessageDto alertMessageDto, Long projectId){
        return AlertMessageDto.builder()
                .alertKey(alertMessageDto.alertKey())
                .projectId(projectId)
                .data(alertMessageDto.data())
                .build();
    }



}
