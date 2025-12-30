package com.org.server.project.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectEnterAnsDto {


    @Schema(description = "프로젝트 id값.")
    private Long projectId;
    @Schema(description = "프로젝트 이름.")
    private String title;
    @Schema(description ="해당 프로젝트의 chatroomId")
    private Long chatRoomId;
    @Schema(example ="yyyy.MM.dd" ,description = "프로젝트 생성 일자")
    private String createDate;
    @Builder
    public ProjectEnterAnsDto(Long projectId, String title, Long chatRoomId, String createDate) {
        this.projectId = projectId;
        this.title = title;
        this.chatRoomId = chatRoomId;
        this.createDate = createDate;
    }
}
