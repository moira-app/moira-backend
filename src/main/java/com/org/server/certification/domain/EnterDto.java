package com.org.server.certification.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EnterDto {

    @NotNull
    @Schema(description = "meet 관련이면 meetid , ticket관련이면 ticket id를 넣어주세요")
    private Long id;
    @Schema(description = "필요시에 필요한 value값 예를 들어서 alias 값같은걸 넣어주세요")
    private String value;

}
