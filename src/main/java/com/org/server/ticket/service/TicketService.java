package com.org.server.ticket.service;


import com.org.server.exception.MoiraException;
import com.org.server.member.service.SecurityMemberReadService;
import com.org.server.project.domain.Project;
import com.org.server.project.repository.ProjectRepository;
import com.org.server.project.service.ProjectService;
import com.org.server.redis.service.RedisUserInfoService;
import com.org.server.ticket.domain.Master;
import com.org.server.ticket.domain.Ticket;
import com.org.server.ticket.domain.TicketDto;
import com.org.server.ticket.repository.AdvanceTicketRepository;
import com.org.server.ticket.repository.TicketRepository;
import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.org.server.member.domain.Member;
import com.org.server.member.repository.MemberRepository;
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;
    private final RedisUserInfoService redisUserInfoService;
    private final AdvanceTicketRepository advanceTicketRepository;
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

    public List<TicketDto> getMemberListOfProject(Long projectId){
       return advanceTicketRepository.getMemberList(projectId);
    }

    public void nextMaster(Long projectId,Long memberId){
        Optional<Ticket> t=ticketRepository.findByMemberIdAndProjectId(projectId,memberId);
        if(t.isEmpty()||t.get().getDeleted()){
            throw new MoiraException("적법하지 않은 적임자입니다",HttpStatus.BAD_REQUEST);
        }
        t.get().updateMaster(Master.MASTER);
    }

    public boolean checkIsMaster(Long projectId,Long memberId){

        Ticket t=ticketRepository
                .findByMemberIdAndProjectId(memberId,projectId).get();

        return t.getMaster().equals(Master.MASTER);
    }


    public void saveTicket(Ticket t){
        ticketRepository.save(t);
    }
}

