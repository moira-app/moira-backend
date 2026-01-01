package com.org.server.s3.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ImgAnsDto {

    @Schema(description = "이미지 업로드 url")
    private String putUrl;
    @Schema(description = "이미지 get url")
    private String getUrl;
    @Schema(description = "변경된 이미지의 타입")
    private ImgType imgType;
    @Schema(description = "변경된 대상의 id값")
    private Long refId;


    @Builder
    public ImgAnsDto(String putUrl, String getUrl,ImgType imgType,Long refId) {
        this.putUrl = putUrl;
        this.getUrl = getUrl;
        this.imgType=imgType;
        this.refId=refId;
    }
}
