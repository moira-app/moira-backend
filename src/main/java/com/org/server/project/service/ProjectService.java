package com.org.server.project.service;



import com.org.server.exception.MoiraException;
import com.org.server.member.domain.Member;
import com.org.server.member.service.SecurityMemberReadService;
import com.org.server.project.domain.Project;
import com.org.server.project.repository.ProjectRepository;
import com.org.server.ticket.domain.Ticket;
import com.org.server.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final SecurityMemberReadService securityMemberReadService;
    @Lazy
    private final TicketService ticketService;

    public Boolean checkProject(Long id){
        Optional<Project> p=projectRepository.findById(id);
        if(p.isEmpty()||p.get().getDeleted()){
            return false;
        }
        return true;
    }
    public String createProject(String title){
        UUID url=UUID.randomUUID();
        Project project=new Project(title,url.toString());
        project=projectRepository.save(project);
        Member member=securityMemberReadService.securityMemberRead();
        Ticket ticket=Ticket.builder()
                .projectId(project.getId())
                .memberId(member.getId())
                .alias(member.getNickName())
                .build();
        ticketService.saveTicket(ticket);
        return url.toString();
    }
    public void delProject(Long id){
        Optional<Project> p=projectRepository.findById(id);
        if(p.isEmpty()||p.get().getDeleted()){
            throw new MoiraException("없는 프로젝트입니다", HttpStatus.BAD_REQUEST);
        }
        p.get().updateDeleted();
    }
}
