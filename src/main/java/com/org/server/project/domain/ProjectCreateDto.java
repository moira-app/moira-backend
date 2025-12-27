package com.org.server.project.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectCreateDto {
    private String title;
    private String createDate;
}
