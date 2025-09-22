package com.org.server.whiteBoardAndPage.domain;


import lombok.Getter;

@Getter
public class PageDto {
    private String pageName;
    private Long pageId;

    public PageDto(String pageName, Long pageId) {
        this.pageName = pageName;
        this.pageId = pageId;
    }
}
