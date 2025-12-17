package com.org.server.certification.repository;


import com.org.server.chat.domain.ChatType;
import com.org.server.chat.domain.QChatRoom;
import com.org.server.member.domain.Member;
import com.org.server.project.domain.ProjectDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import com.querydsl.core.types.Projections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.org.server.chat.domain.QChatRoom.*;
import static com.org.server.project.domain.QProject.project;
import static com.org.server.ticket.domain.QTicket.*;



@Repository
@RequiredArgsConstructor

public class ProjectCertRepo {

    private final JPAQueryFactory queryFactory;

    public List<ProjectDto> getProjectList(Member m){
       return queryFactory
                .select(Projections.constructor(ProjectDto.class,
                        project.id,
                        project.title,
                        chatRoom.id,
                        project.projectUrl
                ))
                .from(ticket)
                .join(project)
                .on(project.id.eq(ticket.projectId))
                .join(chatRoom)
                .on(chatRoom.refId.eq(project.id)
                        .and(chatRoom.chatType.eq(ChatType.PROJECT)))
                .where(ticket.memberId.eq(m.getId()).and(project.deleted.isFalse()))
                .fetch();
    }
}
