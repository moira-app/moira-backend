package com.org.server.eventListener.domain;

import lombok.Builder;

import java.util.Map;

@Builder
public record MemberAlertMessageDto(
        String memberId,
        AlertKey alertKey,
        Map<String,Object> data
) {
}
