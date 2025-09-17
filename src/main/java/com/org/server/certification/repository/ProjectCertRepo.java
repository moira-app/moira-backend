package com.org.server.certification.repository;

import com.org.server.member.domain.Member;
import com.org.server.project.domain.ProjectDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;

import com.querydsl.core.types.Projections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import static com.org.server.project.domain.QProject.project;
import static com.org.server.ticket.domain.QTicket.*;

@Repository
@RequiredArgsConstructor

public class ProjectCertRepo {

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
