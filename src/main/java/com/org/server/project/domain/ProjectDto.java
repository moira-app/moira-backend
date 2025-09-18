package com.org.server.project.domain;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectDto {


    private Long id;
    @NotBlank(message = "비어선 안됩니다")
    @Max(value =15,message = "최대 15글가까지입니다")
    private String title;

    public ProjectDto(Long id, String title) {
        this.id = id;
        this.title = title;
    }
}
