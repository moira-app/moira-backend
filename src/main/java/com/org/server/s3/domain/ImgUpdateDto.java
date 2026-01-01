package com.org.server.s3.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ImgUpdateDto {

    @NotNull(message = "프로필 이미지는 필수입니다.")
    @Schema(description = "프로필 이미지 파일 이름입니다")
    private String fileName;
    @NotNull(message = "contentType은 필수입니다.")
    @Schema(description = "contentType")
    private String contentType;

    @Builder
    public ImgUpdateDto(String fileName, String contentType) {
        this.fileName = fileName;
        this.contentType = contentType;
    }
}
