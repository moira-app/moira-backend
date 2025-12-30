package com.org.server.ticket.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TicketInfoDto {

    private Long memberId;
    @Schema(description = "해당 프로젝트내 에서 유저에게 비춰질 이름을 의미합니다. 입력을 안하고 넘어가면 그냥 유저이름을 넣어주세요")
    private String alias;
    private Master master;
}
