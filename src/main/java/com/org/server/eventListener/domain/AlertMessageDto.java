package com.org.server.eventListener.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Map;

@Builder
public record AlertMessageDto(


        @Schema(description = "알림이 어떤 종류의 알림인지를 보여줍니다.")
        AlertKey alertKey,
        Long projectId,
        @Schema(description = "key:value꼴로 저장된 데이터를 바탕으로 알림의 데이터를 client사이드에 적용합니다.")
        Map<String,Object> data
) {
}
