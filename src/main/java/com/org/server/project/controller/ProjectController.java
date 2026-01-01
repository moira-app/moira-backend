package com.org.server.project.controller;


import com.org.server.project.domain.ProjectCreateDto;
import com.org.server.project.domain.ProjectInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import com.org.server.project.service.ProjectService;
import com.org.server.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
@Tag(name = "프로젝트 생성 api",description = "프로젝트를 생성하는것만 존재하는 api입니다.")
public class ProjectController {

    private final ProjectService projectService;



    @Operation(summary = "프로젝트 생성", description ="프로젝트를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    headers = {
                            @Header(name = "Authorization", description = "Bearer [Access JWT 토큰]",
                                    schema = @Schema(type = "string")),
                    },
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "권한이 부족합니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponseUtil.class)))
    })
    @Parameter(name = "Authorization",
            description = "요청시 토큰값을 넣어주셔야됩니다.",
            required = true,
            example = "Bearer [tokenvalue]",
            in = ParameterIn.HEADER)
    @PostMapping("/create")
    public ResponseEntity<ApiResponseUtil<ProjectInfoDto>> createProject(@RequestBody ProjectCreateDto projectCreateDto){
        return ResponseEntity.ok(
                ApiResponseUtil
                        .CreateApiResponse("ok",projectService.createProject(projectCreateDto)));
    }
}
