package com.org.server.certification.controller;


import com.org.server.certification.repository.ProjectCertRepo;
import com.org.server.certification.service.ProjectCertService;
import com.org.server.meet.domain.MeetConnectDto;
import com.org.server.meet.domain.MeetDto;
import com.org.server.project.domain.ProjectDto;
import com.org.server.project.service.ProjectService;
import com.org.server.ticket.domain.TicketDto;
import com.org.server.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projectCert")
@RequiredArgsConstructor
public class ProjectCertificationController {


    private final ProjectCertService projectCertService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<ProjectDto>>> getProjectList(){
        return ResponseEntity.ok(ApiResponse.CreateApiResponse("ok",
                projectCertService.getProejctList()));
    }

    @PostMapping("/{projectId}/create")
    public ResponseEntity<ApiResponse<String>> createTicket(
            @PathVariable(name = "projectId") Long projectId,
            @RequestBody TicketDto ticketDto){
        projectCertService.createTicket(ticketDto,projectId);
        return ResponseEntity.ok(ApiResponse.CreateApiResponse("ok",null));
    }

    @PostMapping("/{projectId}/change/alias/{alias}")
    public ResponseEntity<ApiResponse<String>> changeAlias(@PathVariable(name = "alias")
                                                           String alias, @PathVariable(name ="projectId")
                                                           Long projectId){
        projectCertService.changeAlias(alias,projectId);
        return ResponseEntity.ok(ApiResponse.CreateApiResponse("ok",null));
    }

    @GetMapping("/{projectId}/checkIn/meet/{meetId}")
    public ResponseEntity<ApiResponse<MeetConnectDto>> checkIn(
            @PathVariable(name = "projectId")Long projectId,
            @PathVariable(name = "meetId")Long meetId){
        return ResponseEntity.ok(ApiResponse.CreateApiResponse("ok",
                projectCertService.checkIn(meetId,projectId)));
    }

    @PostMapping("/{projectId}/create/meet")
    public ResponseEntity<ApiResponse<String>> createMeet(
            @PathVariable(name = "projectId") Long projectId,
            @RequestBody MeetDto meetDto){
        projectCertService.createMeet(meetDto,projectId);
        return ResponseEntity.ok(ApiResponse.CreateApiResponse("ok",null));
    }
}
