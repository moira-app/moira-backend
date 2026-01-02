package com.org.server.certification.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserForBan {

    @NotNull(message = "차단할 유저의 id값은 존재해야합니다.")
    @Schema(description = "차단할 유저의 id값입니다.")
    private Long memberId;
    public UserForBan(Long memberId) {
        this.memberId = memberId;
    }
}
