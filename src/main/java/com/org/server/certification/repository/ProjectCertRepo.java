package com.org.server.certification.repository;

import com.org.server.exception.MoiraException;
import com.org.server.member.domain.Member;
import com.org.server.project.domain.ProjectDto;
import com.org.server.whiteBoardAndPage.domain.WhiteBoard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import com.querydsl.core.types.Projections;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import static com.org.server.project.domain.QProject.project;
import static com.org.server.ticket.domain.QTicket.*;
import static com.org.server.whiteBoardAndPage.domain.QWhiteBoard.whiteBoard;
import java.util.Optional;


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
                .where(ticket.memberId.eq(m.getId()).and(project.deleted.isFalse()))
                .fetch();
        return projectDtoList;
    }


    public Long getWhiteBoardId(Long projectId){
        Optional<WhiteBoard> board=Optional.ofNullable(queryFactory
                .select(whiteBoard)
                .from(whiteBoard)
                .join(project)
                .on(project.eq(whiteBoard.project))
                .where(project.id.eq(projectId).and(project.deleted.isFalse()))
                .fetchOne());
        if(board.isEmpty()){
            throw new MoiraException("화이트 보드가 존재하지않습니다", HttpStatus.BAD_REQUEST);
        }
        return board.get().getId();
    }
}
