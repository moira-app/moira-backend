package com.org.server.ticket.repository;

import com.org.server.ticket.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket,Long>{
    Boolean existsByMemberIdAndProjectId(Long memberId,Long projectId);

    Optional<Ticket> findByMemberIdAndProjectId(Long memberId,Long projectId);

}
