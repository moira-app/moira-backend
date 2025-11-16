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
    @Schema(description = "alias 변경시엔 바꾸고자 하는 별칭값.")
    private String  alias;

}
