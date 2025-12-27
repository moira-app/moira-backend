package com.org.server.project.domain;


import com.org.server.member.domain.MemberDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter

public class ProjectDto {

    @Schema(description ="프로젝트의 id값입니다. 프로젝트 리스트 조회시 들어가는 값입니다.")
    private Long id;
    @Schema(description = "프로젝트 이름입니다. 프로젝트 생성시에는 필수값입니다. 프로젝트 리스트 조회시 들어가는 값입니다.")
    @NotBlank(message = "비어선 안됩니다")
    @Max(value =15,message = "최대 15글가까지입니다")
    private String title;
    private Long chatRoomId;

    @Schema(description = "프로젝트 입장 url입니다. 해당 값을 바탕으로 프로젝트에 입장할수있습니다.")
    private String projectUrl;
    private String createDate;
    @Builder
    public ProjectDto(Long id, String title,Long chatRoomId,String projectUrl,String createDate) {
        this.id = id;
        this.title = title;
        this.chatRoomId=chatRoomId;
        this.projectUrl=projectUrl;
        this.createDate=createDate;
    }
    public ProjectDto(Long id, String title,Long chatRoomId,String projectUrl) {
        this.id = id;
        this.title = title;
        this.chatRoomId=chatRoomId;
        this.projectUrl=projectUrl;
    }


}
