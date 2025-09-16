package com.org.server.project.controller;


import org.springframework.http.ResponseEntity;
import com.org.server.project.service.ProjectService;
import com.org.server.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import com.org.server.project.domain.ProjectDto;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;


    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> createProject(@RequestBody ProjectDto projectDto){
        projectService.createProject(projectDto.getTitle());
        return ResponseEntity.ok(ApiResponse.CreateApiResponse("ok",null));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<ProjectDto>>> getProjectList(){
        return ResponseEntity.ok(ApiResponse.CreateApiResponse("ok",
                projectService.getProjectList()));
    }



}
