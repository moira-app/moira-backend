package com.org.server.project.service;



import com.org.server.chat.domain.ChatType;
import com.org.server.chat.service.ChatRoomService;
import com.org.server.exception.MoiraException;
import com.org.server.member.domain.Member;
import com.org.server.member.service.SecurityMemberReadService;
import com.org.server.project.domain.Project;
import com.org.server.project.repository.ProjectRepository;
import com.org.server.ticket.domain.Master;
import com.org.server.ticket.domain.Ticket;
import com.org.server.ticket.service.TicketService;
import com.org.server.util.DateTimeMapUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ChatRoomService chatRoomService;
    private final ProjectRepository projectRepository;
    private final SecurityMemberReadService securityMemberReadService;
    private final TicketService ticketService;


    public String createProject(String title,String createDate){
        UUID url=UUID.randomUUID();
        Project project=new Project(title,url.toString(),LocalDateTime.parse(createDate,DateTimeMapUtil.FLEXIBLE_NANO_FORMATTER));
        project=projectRepository.save(project);
        Member member=securityMemberReadService.securityMemberRead();
        Ticket ticket=Ticket.builder()
                .projectId(project.getId())
                .memberId(member.getId())
                .alias(member.getNickName())
                .master(Master.MASTER)
                .build();
        ticketService.saveTicket(ticket);
        chatRoomService.ensureRoom(ChatType.PROJECT,project.getId());
        return url.toString();
    }


}
