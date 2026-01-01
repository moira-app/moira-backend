package com.org.server.project.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectCreateDto {
    private String title;

    public ProjectCreateDto(String title) {
        this.title = title;
    }
}
