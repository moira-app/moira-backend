package com.org.server.chat.service;


import com.org.server.chat.domain.ChatEvent;
import com.org.server.chat.domain.ChatType;
import com.org.server.chat.domain.ChatMessage;
import com.org.server.chat.domain.ChatMessageDto;
import com.org.server.chat.repository.ChatMessageAdvanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageAdvanceRepository chatMsgRepository;


    public ChatMessageDto sendMessage(Long roomId, Long senderId, String content, ChatType chatType){
       ChatMessage messsage=chatMsgRepository.createMessage(roomId,senderId,content,chatType);

       return toDto(messsage,ChatEvent.READ);
    }

    public List<ChatMessageDto> getMsgList(String id,Long roomId){
        return chatMsgRepository.findMessages(id,roomId)
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

    public ChatMessageDto updateMsg(String id, String content, Long senderId, LocalDateTime updateDate){
        if(chatMsgRepository.updateMessage(id,content,senderId,updateDate)){
            return toDto(ChatMessage.builder().id(id).content(content).build(),ChatEvent.UPDATE);
        }
        throw new RuntimeException();
    }


    private ChatMessageDto toDto(ChatMessage m, ChatEvent event) {
        return new ChatMessageDto(
                m.getId(),
                event,
                m.getChatType() ,
                m.getRoomId(),
                m.getSenderId(),
                m.getContent(),
                m.getCreateDate(),
                m.getUpdateDate()
        );
    }
}
