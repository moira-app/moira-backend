package com.org.server.ProjectTicketMeetTest;


import com.org.server.chat.domain.ChatRoom;
import com.org.server.member.domain.Member;
import com.org.server.project.domain.Project;
import com.org.server.support.IntegralTestEnv;
import com.org.server.ticket.domain.Master;
import com.org.server.ticket.domain.Ticket;
import com.org.server.ticket.domain.TicketDto;
import com.org.server.ticket.domain.TicketMetaDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class AdvanceTicketRepostest extends IntegralTestEnv {

    Member m1;
    Member m2;
    Project p1;
    ChatRoom c1;
    Ticket t1;
    Ticket t2;


    @BeforeEach
    void setting(){
        m1=createMember(0L);
        m2=createMember(1L);
        p1=createProject("test","test");
        c1=createChatRoom(p1);
        t1=createTicket(m1,p1,"tets", Master.MASTER);
        t2=createTicket(m2,p1,"Test",Master.ELSE);

    }

    @Test
    @DisplayName("기능 테스트")
    void testing(){
        List<TicketDto> ticketDtoList=advanceTicketRepository.getMemberList(p1.getId());
        assertThat(ticketDtoList.size()).isEqualTo(2);
        assertThat(ticketDtoList).extracting("memberId")
                .contains(m1.getId(),m2.getId());
        List<TicketMetaDto> ticketMetaDtoList=advanceTicketRepository.getProjectList(m1.getId());
        assertThat(ticketMetaDtoList.size()).isEqualTo(1);
        assertThat(ticketMetaDtoList).extracting("projectId")
                .contains(p1.getId());
        assertThat(ticketMetaDtoList).extracting("chatRoomId")
                .contains(c1.getId());

    }
}
