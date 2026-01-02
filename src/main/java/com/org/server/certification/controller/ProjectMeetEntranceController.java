package com.org.server.certification.controller;


import com.org.server.certification.domain.AliasDto;
import com.org.server.certification.domain.UserForBan;
import com.org.server.certification.service.ProjectMeetEntranceService;
import com.org.server.meet.domain.*;
import com.org.server.project.domain.ProjectEnterAnsDto;
import com.org.server.project.domain.ProjectInfoDto;
import com.org.server.s3.domain.ImgAnsDto;
import com.org.server.s3.domain.ImgUpdateDto;
import com.org.server.util.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enter")
@RequiredArgsConstructor
@Tag(name = "프로젝트,미팅 참여 api",description = "프로젝트 혹은 미팅에 참여시에 검증 및 관련데이터를 반환하는 api.")
public class ProjectMeetEntranceController {


    private final ProjectMeetEntranceService projectCertService;


    @Operation(summary = "프로젝트 리스트 조회",description = "jwt를 바탕으로 해당 회원이 들어가있는 project list를 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "권한이 부족합니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponseUtil.class)))
    })
    @Parameter(name = "Authorization",
            description = "jwt값",
            required = true,
            in = ParameterIn.HEADER)
    @GetMapping("/list")
    public ResponseEntity<ApiResponseUtil<List<ProjectInfoDto>>> getProjectList(){
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",
                projectCertService.getProejctList()));
    }
    @Operation(summary = "프로젝트 이미지 변경",description = "특정 프로젝트의 이미지를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "권한이 부족합니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponseUtil.class)))
    })
    @Parameter(name = "Authorization",
            description = "jwt값",
            required = true,
            in = ParameterIn.HEADER)
    @Parameter(name = "projectId",
            description = "프로젝트 id값",
            required = true,
            in = ParameterIn.PATH)
    @GetMapping("/{projectId}/change/img")
    public ResponseEntity<ApiResponseUtil<ImgAnsDto>> changeProjectImg(
            @Valid @RequestBody ImgUpdateDto imgUpdateDto,
            @PathVariable(name = "projectId")Long projectId){
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",
                projectCertService.updateProjectImg(imgUpdateDto,projectId)));
    }



    @Operation(summary = "프로젝트 url 검증",description = "프로젝트에 대한 url을 바탕으로 해당 프로젝트에 대한 ticket을 발행 및 필요한 project data를 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "권한이 부족합니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponseUtil.class)))
    })
    @Parameter(name = "Authorization",
            description = "jwt값",
            required = true,
            in = ParameterIn.HEADER)
    @Parameter(name = "projectUrl",
            description = "asdfdvcxv",
            required = true,
            in = ParameterIn.PATH)
    @PostMapping("/project/{projectUrl}")
    public ResponseEntity<ApiResponseUtil<ProjectEnterAnsDto>> enterProjectUrl(
            @PathVariable(name = "projectUrl") String projectUrl
            ,@RequestBody AliasDto enterDto
    ) {
        return ResponseEntity.ok(ApiResponseUtil
                .CreateApiResponse("ok" ,projectCertService.createTicket(projectUrl,enterDto)));

    }


    @Operation(summary = "별칭 변경",description = "프로젝트 내에서 타인에게 보이는 명칭을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "권한이 부족합니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponseUtil.class)))
    })
    @Parameter(name = "Authorization",
            description = "jwt값",
            required = true,
            in = ParameterIn.HEADER)
    @Parameter(name = "projectId",
            description = "현재 작업을 하고있는 프로젝트의 id값",
            required = true,
            in = ParameterIn.PATH)
    @PostMapping("/{projectId}/change/alias")
    public ResponseEntity<ApiResponseUtil<String>> changeAlias(@RequestBody AliasDto enterDto,
                                                               @PathVariable(name ="projectId") Long projectId){
        projectCertService.changeAlias(enterDto.getAlias(),projectId);
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null));
    }
    @Operation(summary = "회의 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "권한이 부족합니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponseUtil.class)))
    })
    @Parameter(name = "Authorization",
            description = "jwt값",
            required = true,
            in = ParameterIn.HEADER)
    @Parameter(name = "projectId",
            description = "현재 작업을 하고있는 프로젝트의 id값",
            required = true,
            in = ParameterIn.PATH)
    @Parameter(name = "meetId",
            description = "현재 입장하려는 미팅의 id값",
            required = true,
            in = ParameterIn.PATH)
    @DeleteMapping("/{projectId}/del/meet/{meetId}")
    public ResponseEntity<ApiResponseUtil<String>> delMeet(
            @PathVariable(name = "meetId")Long meetId,
            @PathVariable(name = "projectId")Long projectId
    ){
        projectCertService.delMeet(meetId,projectId);
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null));
    }

    @Operation(summary = "회의 입장을 할때 체크하는 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "권한이 부족합니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponseUtil.class)))
    })
    @Parameter(name = "Authorization",
            description = "jwt값",
            required = true,
            in = ParameterIn.HEADER)
    @Parameter(name = "projectId",
            description = "현재 입장하려는 프로젝트의 id값",
            required = true,
            in = ParameterIn.PATH)
    @Parameter(name = "meetId",
            description = "현재 입장하려는 미팅의 id값",
            required = true,
            in = ParameterIn.PATH)
    @PostMapping("/{projectId}/checkIn/meet")
    public ResponseEntity<ApiResponseUtil<MeetConnectDto>> checkInMeet(
            @PathVariable(name = "projectId")Long projectId,
            @Valid  @RequestBody MeetEnterDto meetEnterDto){
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",
                projectCertService.checkInMeet(meetEnterDto,projectId)));
    }


    @Operation(summary = "회의 생성 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "권한이 부족합니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponseUtil.class)))
    })
    @Parameter(name = "Authorization",
            description = "jwt값",
            required = true,
            in = ParameterIn.HEADER)
    @Parameter(name = "projectId",
            description = "현재 작업을 하고있는 프로젝트의 id값",
            required = true,
            in = ParameterIn.PATH)
    @PostMapping("/{projectId}/create/meet")
    public ResponseEntity<ApiResponseUtil<String>> createMeet(
            @PathVariable(name = "projectId") Long projectId,
            @RequestBody MeetCreateDto meetDto){
        projectCertService.createMeet(meetDto,projectId);
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null));
    }

    @Operation(summary = "프로젝트삭제 api.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "권한이 부족합니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponseUtil.class)))
    })
    @Parameter(name = "Authorization",
            description = "jwt값",
            required = true,
            in = ParameterIn.HEADER)
    @Parameter(name = "projectId",
            description = "현재 작업을 하고있는 프로젝트의 id값",
            required = true,
            in = ParameterIn.PATH)
    @DeleteMapping("/{projectId}/del")
    public ResponseEntity<ApiResponseUtil<String>> delProject(
            @PathVariable(name = "projectId") Long projectId){
        projectCertService.delProject(projectId);
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null));
    }



    @Operation(summary = "프로젝트에 대한 티켓(접근 권한) 삭제 api.",description = "master 권한을 가진사람은 삭제시 반드시 다음주자가 필요합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "권한이 부족합니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponseUtil.class)))
    })
    @Parameter(name = "Authorization",
            description = "jwt값",
            required = true,
            in = ParameterIn.HEADER)
    @Parameter(name = "projectId",
            description = "현재 작업을 하고있는 프로젝트의 id값",
            required = true,
            in = ParameterIn.PATH)
    @Parameter(name = "nextMaster",
            description = "master권한을 넘길 다음 주자.",
            required = false,
            in = ParameterIn.PATH)
    @DeleteMapping({"/{projectId}/del/ticket/{nextMaster}","/{projectId}/del/ticket"})
    public ResponseEntity<ApiResponseUtil<String>> delTicket(
            @PathVariable(name = "projectId") Long projectId,
            @PathVariable(name="nextMaster",required = false) Long nextMaster){
        projectCertService.delTicket(projectId,nextMaster);
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null));
    }



    @Operation(summary = "미팅 리스트 조회 api", description = "특정 한달동안의 미팅을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "권한이 부족합니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponseUtil.class)))
    })
    @Parameter(name="projectId",
    description = "미팅이 속한 프로젝트 id값",
    required = true,
    in=ParameterIn.PATH)
    @Parameter(name = "Authorization",
            description = "요청시 토큰값을 넣어주셔야됩니다.",
            required = true,
            example = "Bearer [tokenvalue]",
            in = ParameterIn.HEADER)
    @PostMapping("/{projectId}/meetList")
    public ResponseEntity<ApiResponseUtil<List<MeetDateDto>>> getMeetList(
            @PathVariable(name ="projectId") Long projectId,
            @RequestBody @Valid MeetListDto meetListDto){
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",
                projectCertService.getMeetList(meetListDto,projectId)));
    }


    @Operation(summary = "프로젝트 관리자의 차단 기능", description = "관리자 스스로를 제외한 차단 기능입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "권한이 부족합니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponseUtil.class)))
    })
    @Parameter(name="projectId",
            description = "프로젝트 id값",
            required = true,
            in=ParameterIn.PATH)
    @Parameter(name = "Authorization",
            description = "요청시 토큰값을 넣어주셔야됩니다.",
            required = true,
            example = "Bearer [tokenvalue]",
            in = ParameterIn.HEADER)
    @PostMapping("/{projectId}/ban")
    public ResponseEntity<ApiResponseUtil<String>> banUser(
            @PathVariable(name ="projectId") Long projectId,
            @RequestBody @Valid UserForBan user){
        projectCertService.banTicket(projectId,user.getMemberId());
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null
               ));
    }





}