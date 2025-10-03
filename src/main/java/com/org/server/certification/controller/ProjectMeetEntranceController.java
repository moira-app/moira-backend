package com.org.server.certification.controller;


import com.org.server.certification.domain.EnterDto;
import com.org.server.certification.service.ProjectMeetEntranceService;
import com.org.server.meet.domain.MeetConnectDto;
import com.org.server.meet.domain.MeetDto;
import com.org.server.project.domain.ProjectDto;
import com.org.server.ticket.domain.TicketDto;
import com.org.server.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enter")
@RequiredArgsConstructor
public class ProjectMeetEntranceController {


    private final ProjectMeetEntranceService projectCertService;
    @GetMapping("/list")
    public ResponseEntity<ApiResponseUtil<List<ProjectDto>>> getProjectList(){
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",
                projectCertService.getProejctList()));
    }
    @PostMapping("/project/{projectUrl}")
    public ResponseEntity<ApiResponseUtil<String>> enterProjectUrl(
            @PathVariable(name = "projectUrl") String projectUrl
            ,@RequestBody TicketDto ticketDto
    ) {
        projectCertService.createTicket(projectUrl, ticketDto);
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok", null));
    }
    @PostMapping("/{projectId}/change/alias")
    public ResponseEntity<ApiResponseUtil<String>> changeAlias(@RequestBody EnterDto enterDto,
                                                               @PathVariable(name ="projectId") Long projectId){
        projectCertService.changeAlias(enterDto.getValue(),projectId);
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null));
    }
    @PostMapping("/{projectId}/del/meet")
    public ResponseEntity<ApiResponseUtil<String>> delMeet(
            @RequestBody EnterDto enterDto
    ){
        projectCertService.delMeet(enterDto.getId());
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null));
    }

    @GetMapping("/{projectId}/checkIn/meet")
    public ResponseEntity<ApiResponseUtil<MeetConnectDto>> checkInMeet(
            @PathVariable(name = "projectId")Long projectId,
            @RequestBody EnterDto enterDto){
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",
                projectCertService.checkInMeet(enterDto.getId(),projectId)));
    }

    @PostMapping("/{projectId}/create/meet")
    public ResponseEntity<ApiResponseUtil<String>> createMeet(
            @PathVariable(name = "projectId") Long projectId,
            @RequestBody MeetDto meetDto){
        projectCertService.createMeet(meetDto,projectId);
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null));
    }

    @DeleteMapping("/{projectId}/del/ticket")
    public ResponseEntity<ApiResponseUtil<String>> delTicket(
            @RequestBody EnterDto enterDto,
            @PathVariable(name = "ticketId")Long ticketId
    ){
        projectCertService.delTicket(enterDto.getId(),ticketId);
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null));
    }



}