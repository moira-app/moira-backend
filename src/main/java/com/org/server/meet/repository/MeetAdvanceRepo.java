package com.org.server.meet.repository;


import com.org.server.meet.domain.Meet;
import com.org.server.meet.domain.MeetDateDto;
import com.org.server.meet.domain.QMeet;
import com.org.server.project.domain.Project;
import com.org.server.project.domain.QProject;
import com.org.server.ticket.domain.QTicket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.org.server.member.domain.Member;
import java.time.LocalDateTime;
import java.util.List;
import static com.org.server.meet.domain.QMeet.*;
import static com.org.server.member.domain.QMember.member;
import static com.org.server.project.domain.QProject.*;
import static com.org.server.ticket.domain.QTicket.*;


@Repository
@RequiredArgsConstructor
public class MeetAdvanceRepo {

    private final JPAQueryFactory queryFactory;



    public List<MeetDateDto> getMeetList(LocalDateTime startTime, LocalDateTime endTime, Member m){
            return queryFactory.select(Projections.constructor(MeetDateDto.class,
                            meet.id,
                            project.id,
                            project.title,
                            meet.startTime.stringValue()
                            ))
                    .from(ticket)
                    .join(project)
                    .on(project.eq(ticket.project))
                    .join(member)
                    .on(member.eq(ticket.member))
                    .join(meet)
                    .on(meet.project.eq(project))
                    .where(meet.startTime.goe(startTime).and(meet.startTime.lt(endTime))
                            .and(member.eq(m)))
                    .fetch();

    }

}
