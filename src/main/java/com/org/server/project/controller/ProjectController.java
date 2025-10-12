package com.org.server.project.controller;


import org.springframework.http.ResponseEntity;
import com.org.server.project.service.ProjectService;
import com.org.server.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import com.org.server.project.domain.ProjectDto;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    @PostMapping("/create")
    public ResponseEntity<ApiResponseUtil<String>> createProject(@RequestBody ProjectDto projectDto){
        return ResponseEntity.ok(
                ApiResponseUtil
                        .CreateApiResponse(projectService.createProject(projectDto.getTitle()),null));
    }
}
