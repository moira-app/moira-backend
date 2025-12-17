package com.org.server.chat.domain;

import lombok.Builder;


@Builder
public record ChatMessageDto(
        String id,
        ChatEvent chatEvent,
        Long roomId,
        Long senderId,
        String content,
        String createDate,
        String updateDate
) {

}
