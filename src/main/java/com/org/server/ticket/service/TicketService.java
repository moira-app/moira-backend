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
   public Boolean checkIn(Long projectId,Long memberId){
        return ticketRepository.existsByMemberIdAndProjectId(memberId,projectId);
   }
}
