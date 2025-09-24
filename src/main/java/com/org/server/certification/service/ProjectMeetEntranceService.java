package com.org.server.certification.service;

import com.org.server.exception.MoiraException;
import com.org.server.meet.domain.Meet;
import com.org.server.meet.domain.MeetConnectDto;
import com.org.server.meet.domain.MeetDto;
import com.org.server.meet.repository.MeetRepository;
import com.org.server.meet.service.MeetService;
import com.org.server.member.service.SecurityMemberReadService;
import com.org.server.project.domain.Project;
import com.org.server.project.domain.ProjectDto;
import com.org.server.project.repository.ProjectRepository;
import com.org.server.ticket.domain.Ticket;
import com.org.server.ticket.domain.TicketDto;
import com.org.server.ticket.service.TicketService;
import com.org.server.util.DateTimeMapUtil;
import com.org.server.util.RandomCharSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.org.server.member.domain.Member;
import com.org.server.certification.repository.ProjectCertRepo;
import org.springframework.transaction.annotation.Transactional;
import com.org.server.member.repository.MemberRepository;
import com.org.server.ticket.repository.TicketRepository;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProjectMeetEntranceService {

    private final ProjectCertRepo projectCertRepo;
    private final SecurityMemberReadService securityMemberReadService;
    private final MeetService meetService;
    private final ProjectRepository projectRepository;
    private final TicketService ticketService;

    public List<ProjectDto> getProejctList(){
        Member m=securityMemberReadService.securityMemberRead();
        return projectCertRepo.getProjectList(m);
    }
    public void createTicket(TicketDto ticketDto,Long projectId){
        Member m=securityMemberReadService.findMemberCheckDelAndNull(ticketDto.getEmail());
        if(ticketService.checkByProjectIdAndMemberId(projectId,m.getId())){
            throw new MoiraException("이미 초대되었거나 혹은 퇴출된 유저입니다", HttpStatus.BAD_REQUEST);
        }
        Ticket ticket= new Ticket(projectId,m.getId(),ticketDto.getAlias());
        ticketService.saveTicket(ticket);
    }
    public void changeAlias(String alias,Long projectId){
        Member m=securityMemberReadService.securityMemberRead();
        Ticket ticket= ticketService.findByProjectIdAndMemberId(projectId,m.getId());
        ticket.updateAlias(alias);
        return ;
    }
    public MeetConnectDto checkInMeet(Long id,Long projectId) {
        LocalDateTime now = LocalDateTime.now();
        Meet meet = meetService.findById(id);

        if (now.isBefore(meet.getStartTime())||now.isAfter(meet.getEndTime())) {
            throw new MoiraException("회의 입장시간 전이거나 이후 입니다.", HttpStatus.BAD_REQUEST);
        }
        Member m = securityMemberReadService.securityMemberRead();
        Ticket t=ticketService.findByProjectIdAndMemberId(projectId,m.getId());
        return new MeetConnectDto(meet.getMeetName(),
                t.getAlias()==null ? m.getNickName() :t.getAlias());
    }
    public void createMeet(MeetDto meetDto,Long projectId){
        Project project=projectRepository.findById(projectId).get();
        LocalDateTime startTime=LocalDateTime.parse(meetDto.getStartTime(),
                DateTimeMapUtil.formatByDot);
        LocalDateTime endTime=LocalDateTime.parse(meetDto.getEndTime(),
                DateTimeMapUtil.formatByDot);
        Meet m=Meet.builder()
                .project(project)
                .startTime(startTime)
                .meetName(meetDto.getMeetName())
                .endTime(endTime)
                .build();
        meetService.saveMeet(m);
    }

    public void delTicket(Long projectId,Long ticketId){
        ticketService.delTicket(projectId,ticketId);
    }

    public void delMeet(Long meetId){
        meetService.delMeet(meetId);
    }
}
