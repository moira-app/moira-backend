package com.org.server.member.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;


@Getter

public class NormalLoginDto {

    @Schema(description = "필수값입니다.")
    private String mail;
    @Schema(description = "필수값입니다.")
    private String password;

    public NormalLoginDto(String mail, String password) {
        this.mail = mail;
        this.password = password;
    }
}
