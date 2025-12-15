package com.org.server.scheduler.repository;


import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.org.server.member.domain.QMember.member;
import static com.org.server.project.domain.QProject.project;
import static com.org.server.ticket.domain.QTicket.ticket;

@Repository
@RequiredArgsConstructor
public class SchedulerRepository {

    private final JPAQueryFactory queryFactory;
    public void delTicketQuery(){
        JPQLQuery<Long> delIds= JPAExpressions.select(ticket.id)
                .from(ticket)
                .join(member)
                .on(member.id.eq(ticket.memberId))
                .join(project)
                .on(project.id.eq(ticket.projectId))
                .where(ticket.deleted.isFalse().and(member.deleted.isTrue().or(project.deleted.isTrue())));

        queryFactory.update(ticket)
                .set(ticket.deleted,true)
                .where(ticket.id.in(delIds))
                .execute();
    }
}
