package com.org.server.ticket.repository;


import com.org.server.chat.domain.ChatType;
import com.org.server.ticket.domain.TicketInfoDto;
import com.org.server.ticket.domain.TicketMetaDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.org.server.chat.domain.QChatRoom.*;
import static com.org.server.member.domain.QMember.*;
import static com.org.server.ticket.domain.QTicket.*;

@Repository
@RequiredArgsConstructor
public class AdvanceTicketRepository {

    private final JPAQueryFactory queryFactory;
    public List<TicketInfoDto> getMemberList(Long projectId){
        return queryFactory.select(
                Projections.constructor(TicketInfoDto.class,
                        member.id,
                        ticket.alias,
                        ticket.master)
                )
                .from(ticket)
                .join(member)
                .on(member.id.eq(ticket.memberId))
                .where(ticket.projectId.eq(projectId))
                .fetch();
    }

    public List<TicketMetaDto> getProjectList(Long memberId){
        return queryFactory.select(
                Projections.constructor(TicketMetaDto.class,
                        ticket.projectId,chatRoom.id)
                )
                .from(ticket)
                .join(member)
                .on(member.id.eq(ticket.memberId))
                .join(chatRoom)
                .on(chatRoom.refId.eq(ticket.projectId).and(chatRoom.chatType.eq(ChatType.PROJECT)))
                .where(ticket.memberId.eq(memberId))
                .fetch();
    }
}
