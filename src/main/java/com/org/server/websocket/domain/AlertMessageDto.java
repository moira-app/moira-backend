package com.org.server.websocket.domain;

import com.org.server.chat.domain.ChatEvent;
import com.org.server.chat.domain.ChatType;
import lombok.Builder;

import java.util.Map;

@Builder
public record AlertMessageDto(
        AlertKey alertKey,
        Long projectId,
        Map<String,Object> data
) {
}
