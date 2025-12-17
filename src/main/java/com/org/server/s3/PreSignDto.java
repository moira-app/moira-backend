package com.org.server.s3;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PreSignDto {
    @Schema(description = "put일땐 필수입니다.whiteboard 안에 들어가는 이미지는 whiteboard값으로 그외에는 보통 이미지 밈타입을 주세요.")
    private String contentType;
    @NotNull(message = "파일이름은 필수값입니다.")
    @Schema(description = "put일땐 파일의 이름을 get일땐 파일 주소를 주세요.")
    private String fileName;
}
