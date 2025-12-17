package com.org.server.ticket.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {

    private Long memberId;
    @Schema(description = "해당 프로젝트내 에서 유저에게 비춰질 이름을 의미합니다.")
    private String alias;
}
