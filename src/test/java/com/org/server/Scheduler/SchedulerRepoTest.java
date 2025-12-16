package com.org.server.Scheduler;


import com.org.server.member.domain.Member;
import com.org.server.member.service.SecurityMemberReadService;
import com.org.server.project.domain.Project;
import com.org.server.support.IntegralTestEnv;
import com.org.server.ticket.domain.Master;
import com.org.server.ticket.domain.Ticket;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.bson.io.BsonOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class SchedulerRepoTest extends IntegralTestEnv {



    @Autowired
    EntityManager entityManager;
    Member normalMember;
    Member normalMember2;
    Member delMember;
    Project p1;
    Project p2;
    Project p3;

    Ticket t1;
    Ticket t2;
    Ticket t3;

    @BeforeEach
    void test(){
        normalMember=createMember(0L);
        normalMember2=createMember(1L);
        delMember=createMember(2L);
        p1=createProject("test1","dfdsfds");
        p2=createProject("test2","dfdsffd");
        p3=createProject("test3","dfsdfddsfsd");
        t1=createTicket(normalMember,p1,"test", Master.MASTER);
        t2=createTicket(normalMember2,p2,"test", Master.MASTER);
        t3=createTicket(delMember,p3,"test", Master.MASTER);
    }


    @Test
    @DisplayName("티켓 벌크 삭제 테스트")
    @Transactional
    void testingTicketDel(){
        Mockito.when(securityMemberReadService.securityMemberRead())
                .thenReturn(delMember)
                .thenReturn(normalMember)
                .thenReturn(normalMember2);
        memberService.delMember();
        System.out.println(normalMember.getId());
        assertThat(ticketRepository.findByMemberIdAndProjectId(normalMember.getId(),p1.getId()).get().getMaster())
                .isEqualTo(Master.MASTER);

        projectCertService.delProject(p1.getId());
        projectCertService.delProject(p2.getId());
        schedulerRepository.delTicketQuery();


        assertThat(projectRepository.findById(p1.getId()).get().getDeleted()).isTrue();
        assertThat(projectRepository.findById(p2.getId()).get().getDeleted()).isTrue();
        assertThat(memberRepository.findById(delMember.getId()).get().getDeleted()).isTrue();

        entityManager.clear();

        List<Ticket> t=ticketRepository.findAll();
        assertThat(t).extracting("deleted")
                .containsOnly(true);
    }
}
