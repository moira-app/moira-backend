package com.org.server.ticket.service;


import com.org.server.exception.MoiraException;
import com.org.server.member.service.SecurityMemberReadService;
import com.org.server.project.domain.Project;
import com.org.server.project.repository.ProjectRepository;
import com.org.server.project.service.ProjectService;
import com.org.server.redis.service.RedisUserInfoService;
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
    private final RedisUserInfoService redisUserInfoService;
    public Boolean checkIn(Long projectId,Long memberId){
        Optional<Ticket> ticket=
                ticketRepository.findByMemberIdAndProjectId(memberId,projectId);
        if(ticket.isEmpty()||ticket.get().getDeleted()){
            return false;
        }
        /*if(projectService.checkProject(ticket.get().getId())){
            return false;
        }*/
        return true;
    }
    public void delTicket(Long projectId,Long memberId){
        Optional<Ticket> ticket=
                ticketRepository.findByMemberIdAndProjectId(memberId,projectId);
        if(ticket.isEmpty()||ticket.get().getDeleted()){
            return ;
        }
        ticket.get().updateDeleted();
        ticketRepository.save(ticket.get());
        redisUserInfoService.delTicketKey(String.valueOf(memberId)
                ,String.valueOf(ticket.get().getId()));
    }
    public Boolean checkByProjectIdAndMemberId(Long projectId,Long memberId){
        return ticketRepository.existsByMemberIdAndProjectId(memberId,projectId);
    }

    public Ticket findByProjectIdAndMemberId(Long projectId,Long memberId){
        Optional<Ticket> ticket=ticketRepository.findByMemberIdAndProjectId(memberId,projectId);
        return ticket.get();
    }
    public void saveTicket(Ticket t){
        ticketRepository.save(t);
    }
}

