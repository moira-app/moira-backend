package com.org.server.websocket.domain;

import com.org.server.chat.domain.ChatEvent;
import com.org.server.chat.domain.ChatType;
import jakarta.xml.bind.annotation.XmlElementDecl;
import lombok.Builder;

import java.util.Map;

@Builder
public record AlertMessageDto(
        AlertKey alertKey,
        Long projectId,
        Map<String,Object> data
) {
}
