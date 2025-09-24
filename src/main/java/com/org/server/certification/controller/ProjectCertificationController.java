package com.org.server.certification.controller;


import com.org.server.certification.service.ProjectCertService;
import com.org.server.meet.domain.MeetConnectDto;
import com.org.server.meet.domain.MeetDto;
import com.org.server.project.domain.ProjectDto;
import com.org.server.ticket.domain.TicketDto;
import com.org.server.util.ApiResponseUtil;
import com.org.server.whiteBoardAndPage.domain.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/projectCert")
@RequiredArgsConstructor
public class ProjectCertificationController {


    private final ProjectCertService projectCertService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponseUtil<List<ProjectDto>>> getProjectList(){
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",
                projectCertService.getProejctList()));
    }
    @PostMapping("/{projectId}/create")
    public ResponseEntity<ApiResponseUtil<String>> createTicket(
            @PathVariable(name = "projectId") Long projectId,
            @RequestBody TicketDto ticketDto){
        projectCertService.createTicket(ticketDto,projectId);
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null));
    }

    @PostMapping("/{projectId}/change/alias/{alias}")
    public ResponseEntity<ApiResponseUtil<String>> changeAlias(@PathVariable(name = "alias")
                                                           String alias, @PathVariable(name ="projectId")
                                                           Long projectId){
        projectCertService.changeAlias(alias,projectId);
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null));
    }

    @GetMapping("/{projectId}/checkIn/meet/{meetId}")
    public ResponseEntity<ApiResponseUtil<MeetConnectDto>> checkIn(
            @PathVariable(name = "projectId")Long projectId,
            @PathVariable(name = "meetId")Long meetId){
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",
                projectCertService.checkIn(meetId,projectId)));
    }

    @PostMapping("/{projectId}/create/meet")
    public ResponseEntity<ApiResponseUtil<String>> createMeet(
            @PathVariable(name = "projectId") Long projectId,
            @RequestBody MeetDto meetDto){
        projectCertService.createMeet(meetDto,projectId);
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null));
    }

    @GetMapping("/{projectId}/get/page/{pageId}")
    public ResponseEntity<StreamingResponseBody> getPageData(
            @PathVariable(name = "pageId") Long pageId){
        return projectCertService.getPageDataByStreaming(pageId);
    }

    @GetMapping("/{projectId}/get/pageList")
    public ResponseEntity<ApiResponseUtil<List<PageDto>>> getPageList(
            @PathVariable(name = "projectId") Long projectId
    ){
        return ResponseEntity.ok(
                ApiResponseUtil.CreateApiResponse("ok"
                ,projectCertService.getPageList(projectId)));
    }

    @PostMapping("/{projectId}/save/page/{pageId}")
    public ResponseEntity<ApiResponseUtil<String>> savePageData(
            @PathVariable(name = "pageId") Long pageId,
            @RequestParam("file") MultipartFile file
    ){
        projectCertService.savePageData(pageId,file);
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null));
    }
}
