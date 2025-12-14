package com.org.server.chat.domain;

import com.org.server.chat.domain.ChatType;

import java.time.LocalDateTime;

public record ChatMessageDto(
        String id,
        ChatEvent chatEvent,
        ChatType scope,
        Long roomId,
        Long senderId,
        String content,
        String createDate,
        String updateDate
) {

}
