package com.org.server.chat.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;


@Builder
public record ChatMessageDto(

        @Schema(description = "채팅 id값입니다.")
        String id,
        @Schema(description = "응답받는 입장에서 상황에따라 READ(일반적인 발행),UPDATE,DELETE값입니다.")
        ChatEvent chatEvent,
        @Schema(description = "채팅방 id값입니다.")
        Long roomId,
        @Schema(description = "보낸이의 회원 id값입니다.")
        Long senderId,
        @Schema(description = "내용 입니다.")
        String content,
        @Schema(example = "yyyy-MM-dd HH:mm:ss",description = "생성 일자입니다. 항상 포함되어있습니다.")
        String createDate,
        @Schema(example="yyyy-MM-dd HH:mm:ss",description = "수정을 하였다면 값이 존재하고 없다면 null값입니다.")
        String updateDate
) {

}
