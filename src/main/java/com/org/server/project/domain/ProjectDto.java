package com.org.server.project.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectDto {


    @Schema(description ="프로젝트의 id값입니다. 프로젝트 리스트 조회시 들어가는 값입니다.")
    private Long id;
    @Schema(description = "프로젝트 이름입니다. 프로젝트 생성시에는 필수값입니다. 프로젝트 리스트 조회시 들어가는 값입니다.")
    @NotBlank(message = "비어선 안됩니다")
    @Max(value =15,message = "최대 15글가까지입니다")
    private String title;

    public ProjectDto(Long id, String title) {
        this.id = id;
        this.title = title;
    }
}
