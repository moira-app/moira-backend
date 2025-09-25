package com.org.server.whiteBoardAndPage.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PageDto {

    @Schema(description = "해당값은 request시엔 빈값이고 response로 돌려줄때 s3의 url를 돌려줍니다.")
    private String value;
    @NotNull
    @Schema(description = "해당값은 비어선 안됩니다")
    private Long pageId;

    public PageDto(String value, Long pageId) {
        this.value=value;
        this.pageId = pageId;
    }
}
