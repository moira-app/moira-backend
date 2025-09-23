package com.org.server.whiteBoardAndPage.domain;


import lombok.Getter;

@Getter
public class PageDto {
    private String value;
    private Long pageId;

    public PageDto(String value, Long pageId) {
        this.value=value;
        this.pageId = pageId;
    }
}
