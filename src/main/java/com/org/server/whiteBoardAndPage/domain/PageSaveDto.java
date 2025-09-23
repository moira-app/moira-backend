package com.org.server.whiteBoardAndPage.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class PageSaveDto {

    private Long pageId;
    private String fileName;
    private String fileLocation;


    @Builder
    public PageSaveDto(Long pageId,String fileName, String fileLocation) {
        this.pageId=pageId;
        this.fileName = fileName;
        this.fileLocation = fileLocation;
    }
}
