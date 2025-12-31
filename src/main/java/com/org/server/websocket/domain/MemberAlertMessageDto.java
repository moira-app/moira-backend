package com.org.server.websocket.domain;

import lombok.Builder;

import java.util.Map;

@Builder
public record MemberAlertMessageDto(
        String memberId,
        AlertKey alertKey,
        Map<String,Object> data
) {
}
