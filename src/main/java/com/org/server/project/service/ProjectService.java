package com.org.server.project.service;



import com.org.server.chat.domain.ChatRoom;
import com.org.server.chat.domain.ChatType;
import com.org.server.chat.service.ChatRoomService;
import com.org.server.member.domain.Member;
import com.org.server.member.service.SecurityMemberReadService;
import com.org.server.project.domain.Project;
import com.org.server.project.domain.ProjectInfoDto;
import com.org.server.project.repository.ProjectRepository;
import com.org.server.ticket.domain.Master;
import com.org.server.ticket.domain.Ticket;
import com.org.server.ticket.service.TicketService;
import com.org.server.util.DateTimeMapUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ChatRoomService chatRoomService;
    private final ProjectRepository projectRepository;
    private final SecurityMemberReadService securityMemberReadService;
    private final TicketService ticketService;


    public ProjectInfoDto createProject(String title){
        UUID url=UUID.randomUUID();
        Project project=new Project(title,url.toString());
        project=projectRepository.save(project);
        Member member=securityMemberReadService.securityMemberRead();
        Ticket ticket=Ticket.builder()
                .projectId(project.getId())
                .memberId(member.getId())
                .alias(member.getNickName())
                .master(Master.MASTER)
                .build();
        ticketService.saveTicket(ticket);
        ChatRoom chatRoom=chatRoomService.ensureRoom(ChatType.PROJECT,project.getId());
        return ProjectInfoDto.builder()
                .projectId(project.getId())
                .title(project.getTitle())
                .chatRoomId(chatRoom.getId())
                .alias(member.getNickName())
                .projectUrl(project.getProjectUrl())
                .master(Master.MASTER)
                .build();
    }


}
