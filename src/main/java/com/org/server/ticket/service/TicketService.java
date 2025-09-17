package com.org.server.ticket.service;


import com.org.server.exception.MoiraException;
import com.org.server.member.service.SecurityMemberReadService;
import com.org.server.project.domain.Project;
import com.org.server.project.repository.ProjectRepository;
import com.org.server.ticket.domain.Ticket;
import com.org.server.ticket.domain.TicketDto;
import com.org.server.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.org.server.member.domain.Member;
import com.org.server.member.repository.MemberRepository;
@Service
@RequiredArgsConstructor
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final SecurityMemberReadService securityMemberReadService;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;



    public Boolean checkIn(Long projectId){
        Member m=securityMemberReadService.securityMemberRead();
        return ticketRepository.existsByMemberIdAndProjectId(m.getId(),projectId);
    }

    public void createTicket(TicketDto ticketDto){

        Member m=memberRepository.findByEmail(ticketDto.getEmail()).get();

        if(ticketRepository.existsByMemberIdAndProjectId(m.getId(),ticketDto.getId())){
            throw new MoiraException("이미 초대된 유저입니다", HttpStatus.BAD_REQUEST);
        }
        Ticket ticket= new Ticket(ticketDto.getId(),m.getId(),ticketDto.getAlias());
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
}
