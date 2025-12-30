package com.org.server.project.domain;


import com.org.server.ticket.domain.Master;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class ProjectInfoDto {

    private Long projectId;
    private String title;
    private Long chatRoomId;
    @Schema(description = "초대 코드")
    private String projectUrl;
    @Schema(description = "해당 프로젝트에서 사용하는 별칭")
    private String alias;
    @Schema(description = "해당 프로젝트 내에서 권한")
    private Master master;

    @Builder
    public ProjectInfoDto(Long projectId,  Long chatRoomId,String title, String projectUrl,String alias,Master master) {
        this.projectId = projectId;
        this.title = title;
        this.chatRoomId = chatRoomId;
        this.projectUrl=projectUrl;
        this.alias=alias;
        this.master=master;
    }
}
