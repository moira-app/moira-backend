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
    private final MemberRepository memberRepository;
    private final TicketRepository ticketRepository;
    private final MeetRepository meetRepository;
    private final ProjectRepository projectRepository;

    public List<ProjectDto> getProejctList(){
        Member m=securityMemberReadService.securityMemberRead();
        return projectCertRepo.getProjectList(m);
    }
    public void createTicket(TicketDto ticketDto,Long projectId){

        Member m=memberRepository.findByEmail(ticketDto.getEmail()).get();

        if(ticketRepository.existsByMemberIdAndProjectId(m.getId(),projectId)){
            throw new MoiraException("이미 초대된 유저입니다", HttpStatus.BAD_REQUEST);
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
        if (meet.isEmpty()) {
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
}
