package com.org.server.certification.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AliasDto {

    @NotNull(message = "필수값입니다.")
    @Schema(description = "해당 프로젝트에서 비춰질 이름으로 만약 설정하지않는다면 nickname을 넣어주십쇼.")
    private String  alias;

}
