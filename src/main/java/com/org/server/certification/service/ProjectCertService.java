package com.org.server.certification.service;

import com.org.server.exception.MoiraException;
import com.org.server.meet.domain.Meet;
import com.org.server.meet.domain.MeetConnectDto;
import com.org.server.meet.domain.MeetDto;
import com.org.server.meet.repository.MeetRepository;
import com.org.server.member.service.SecurityMemberReadService;
import com.org.server.project.domain.Project;
import com.org.server.project.domain.ProjectDto;
import com.org.server.project.repository.ProjectRepository;
import com.org.server.ticket.domain.Ticket;
import com.org.server.ticket.domain.TicketDto;
import com.org.server.util.DateTimeMapUtil;
import com.org.server.util.RandomCharSet;
import com.org.server.whiteBoardAndPage.domain.Page;
import com.org.server.whiteBoardAndPage.domain.PageDto;
import com.org.server.whiteBoardAndPage.repository.PageRepo;
import com.org.server.whiteBoardAndPage.repository.WhiteBoardRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import java.util.stream.Collectors;
import com.org.server.member.domain.Member;
import com.org.server.certification.repository.ProjectCertRepo;
import org.springframework.transaction.annotation.Transactional;
import com.org.server.member.repository.MemberRepository;
import com.org.server.ticket.repository.TicketRepository;
import java.io.*;
import org.springframework.http.*;
import org.springframework.web.multipart.MultipartFile;
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProjectCertService {

    private final ProjectCertRepo projectCertRepo;
    private final SecurityMemberReadService securityMemberReadService;
    private final MemberRepository memberRepository;
    private final TicketRepository ticketRepository;
    private final MeetRepository meetRepository;
    private final ProjectRepository projectRepository;
    private final PageRepo pageRepo;
    private final WhiteBoardRepo whiteBoardRepo;

    public List<ProjectDto> getProejctList(){
        Member m=securityMemberReadService.securityMemberRead();
        return projectCertRepo.getProjectList(m);
    }
    public void createTicket(TicketDto ticketDto,Long projectId){

        Member m=memberRepository.findByEmail(ticketDto.getEmail()).get();

        if(ticketRepository.existsByMemberIdAndProjectId(m.getId(),projectId)){
            throw new MoiraException("이미 초대되었거나 혹은 퇴출된 유저입니다", HttpStatus.BAD_REQUEST);
        }
        Ticket ticket= new Ticket(projectId,m.getId(),ticketDto.getAlias());
        ticketRepository.save(ticket);
    }

    public void changeAlias(String alias,Long projectId){
        Member m=securityMemberReadService.securityMemberRead();
        Optional<Ticket> ticket=
                ticketRepository.findByMemberIdAndProjectId(m.getId(),projectId);
        if(ticket.isEmpty()){
            throw new MoiraException("해당 권한이없습니다",HttpStatus.BAD_REQUEST);
        }
        ticket.get().updateAlias(alias);
        return ;
    }

    public MeetConnectDto checkIn(Long id,Long projectId) {
        LocalDateTime now = LocalDateTime.now();
        Optional<Meet> meet = meetRepository.findById(id);
        if (meet.isEmpty()||meet.get().getDeleted()) {
            throw new MoiraException("존재하지 않는 회의입니다", HttpStatus.BAD_REQUEST);
        }
        if (now.isBefore(meet.get().getStartTime())) {
            throw new MoiraException("회의 시간 전입니다", HttpStatus.BAD_REQUEST);
        }
        Member m = securityMemberReadService.securityMemberRead();
        Ticket t=ticketRepository.findByMemberIdAndProjectId(m.getId(),projectId).get();
        return new MeetConnectDto(meet.get().getMeetUrl(), t.getAlias()==null ?
                m.getNickName() :t.getAlias());
    }

    public void createMeet(MeetDto meetDto,Long projectId){
        Project project=projectRepository.findById(projectId).get();
        String meetUrl= RandomCharSet.createRandomName();
        LocalDateTime startTime=LocalDateTime.parse(meetDto.getStartTime(),
                DateTimeMapUtil.formatByDot);
        LocalDateTime endTime=LocalDateTime.parse(meetDto.getEndTime(),
                DateTimeMapUtil.formatByDot);
        Meet m=Meet.builder()
                .project(project)
                .meetUrl(meetUrl)
                .startTime(startTime)
                .endTime(endTime)
                .build();
        meetRepository.save(m);
    }

    public List<PageDto> getPageList(Long projectId) {
        Long boardId = projectCertRepo.getWhiteBoardId(projectId);
        List<Page> pages = pageRepo.findByWhiteBoardIdAndDeleted(boardId,false);
        return pages.stream()
                .map(x -> {
                    return new PageDto(x.getPageName(), x.getId());
                })
                .collect(Collectors.toList());
    }
    public ResponseEntity<StreamingResponseBody> getPageDataByStreaming(Long pageId) {
        Optional<Page> page = pageRepo.findById(pageId);
        if (page.isEmpty()) {
            throw new MoiraException("없는 페이지입니다", HttpStatus.BAD_REQUEST);
        }
        String fileLocation = page.get().getFileLocation();
        File file = new File(fileLocation);
        if(!file.exists()){
            throw new MoiraException("없는 페이지입니다", HttpStatus.BAD_REQUEST);
        }
        StreamingResponseBody streamingResponseBody = OutputStream -> {
            try (InputStream inputStream = new FileInputStream(file)) {
                byte[] buffer = new byte[8192]; // 8KB 버퍼
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    OutputStream.write(buffer, 0, bytesRead);
                    OutputStream.flush(); // 바로바로 클라이언트로 전달
                }
            }
            catch (Exception e){
                log.info("{}:{}",fileLocation,e.getMessage());
            }
        };
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(streamingResponseBody);
    }

    public void savePageData(Long pageId,MultipartFile file){
        Optional<Page> page=pageRepo.findById(pageId);
        if(page.isEmpty()){
            throw new MoiraException("존재 하지 않는 페이지입니다",HttpStatus.BAD_REQUEST);
        }
        File target=new File(page.get().getFileLocation());
        if(!target.exists()){
            throw new MoiraException("존재 하지 않는 페이지입니다",HttpStatus.BAD_REQUEST);
        }
        try(InputStream inputStream=file.getInputStream();
            OutputStream out = new FileOutputStream(target)){
            {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        }
        catch (Exception e){
            throw new MoiraException("파일저장중 에러발생",HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
