package com.org.server.chat.service;


import com.org.server.chat.domain.ChatEvent;
import com.org.server.chat.domain.ChatMessage;
import com.org.server.chat.domain.ChatMessageDto;
import com.org.server.chat.repository.ChatMessageAdvanceRepository;
import com.org.server.util.DateTimeMapUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageAdvanceRepository chatMsgRepository;


    public ChatMessageDto sendMessage(Long roomId, Long senderId, String content,String createDate){
       ChatMessage message=chatMsgRepository.createMessage(roomId,senderId,content,createDate);
       return toDto(message,ChatEvent.READ);
    }

    public List<ChatMessageDto> getMsgList(String id,Long roomId,String createDate){
        return chatMsgRepository.findMessages(id,roomId,createDate)
                .stream().map(x->{
                    return toDto(x,ChatEvent.READ);
                }).collect(Collectors.toList());
    }

    public ChatMessageDto delMsg(String id,Long senderId){
        if(chatMsgRepository.delMessage(id,senderId)){
            return toDto(ChatMessage.builder().id(id).build(),ChatEvent.DELETE);
        }
        throw new RuntimeException();
    }

    public ChatMessageDto updateMsg(String id, String content, Long senderId, String updateDate){
        if(chatMsgRepository.updateMessage(id,content,senderId,updateDate)){
            return toDto(ChatMessage.builder().id(id).content(content)
                    .updateDate(LocalDateTime.parse(updateDate, DateTimeMapUtil.FLEXIBLE_NANO_FORMATTER)).build(),ChatEvent.UPDATE);
        }
        throw new RuntimeException();
    }


    private ChatMessageDto toDto(ChatMessage m, ChatEvent event) {
        return new ChatMessageDto(
                m.getId(),
                event,
                m.getRoomId(),
                m.getSenderId(),
                m.getContent(),
                m.getCreateDate()==null ? null:DateTimeMapUtil.provideTimePattern2(m.getCreateDate()),
                m.getUpdateDate()==null ? null:DateTimeMapUtil.provideTimePattern2(m.getUpdateDate())
        );
    }
}
