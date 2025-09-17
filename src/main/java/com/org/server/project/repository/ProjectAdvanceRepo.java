package com.org.server.project.repository;


import com.org.server.project.domain.Project;
import com.org.server.project.domain.ProjectDto;
import com.org.server.ticket.domain.QTicket;
import com.querydsl.core.types.Projections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.org.server.member.domain.Member;
import java.util.List;

import static com.org.server.member.domain.QMember.member;
import static com.org.server.project.domain.QProject.project;
import static com.org.server.ticket.domain.QTicket.*;

@Repository
@RequiredArgsConstructor
public class ProjectAdvanceRepo {

    private final JPAQueryFactory queryFactory;

    public List<ProjectDto> getProjectList(Member m){
            List<ProjectDto> projectDtoList=queryFactory
                    .select(Projections.constructor(ProjectDto.class,
                            project.id,
                            project.title
                            ))
                    .from(ticket)
                    .join(project)
                    .on(project.id.eq(ticket.projectId))
                    .where(ticket.memberId.eq(m.getId()))
                    .fetch();
            return projectDtoList;
    }
}
