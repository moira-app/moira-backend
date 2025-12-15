package com.org.server.ticket.repository;


import com.org.server.meet.domain.MeetDateDto;
import com.org.server.member.domain.QMember;
import com.org.server.project.domain.Project;
import com.org.server.ticket.domain.QTicket;
import com.org.server.ticket.domain.TicketDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.org.server.member.domain.QMember.*;
import static com.org.server.ticket.domain.QTicket.*;

@Repository
@RequiredArgsConstructor
public class AdvanceTicketRepository {

    private final JPAQueryFactory queryFactory;
    public List<TicketDto> getMemberList(Long projectId){
        return queryFactory.select(
                Projections.constructor(TicketDto.class,
                        member.id,
                        ticket.alias)
                )
                .from(ticket)
                .join(member)
                .on(member.id.eq(ticket.memberId))
                .where(ticket.projectId.eq(projectId))
                .fetch();
    }
}
