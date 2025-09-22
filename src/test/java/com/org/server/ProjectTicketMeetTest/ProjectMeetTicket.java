package com.org.server.ProjectTicketMeetTest;


import com.org.server.meet.domain.Meet;
import com.org.server.meet.domain.MeetConnectDto;
import com.org.server.meet.domain.MeetDateDto;
import com.org.server.meet.domain.MeetDto;
import com.org.server.project.domain.Project;
import com.org.server.project.domain.ProjectDto;

import com.org.server.support.IntegralTestEnv;
import com.org.server.ticket.domain.Ticket;
import com.org.server.util.DateTimeMapUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import com.org.server.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

public class ProjectMeetTicket extends IntegralTestEnv {


    Project p;

    Project p2;

    Ticket t;

    Member m;

    String monthCurrent;
    String monthFuture;

    private static DataSource testDataSource;



    @BeforeAll
    static void setupH2CustomFunctions(@Autowired DataSource dataSource) {
        testDataSource = dataSource; //
        try (Connection conn = testDataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE ALIAS IF NOT EXISTS DATE_FORMAT FOR 'com.org.server.util.DateTimeMapUtil.dateFormat'");
            System.out.println("H2에 DATE_FORMAT 함수 별칭이 성공적으로 등록되었습니다.");
        } catch (SQLException e) {
            System.err.println("H2 함수 별칭 등록 중 오류 발생: " + e.getMessage());
            e.printStackTrace();

            throw new RuntimeException("Failed to register H2 DATE_FORMAT alias", e);
        }
    }




    @BeforeEach
    void settings(){

        m=createMember(1L);
        Member m2=createMember(2L);
        Mockito.when(securityMemberReadService.securityMemberRead())
                .thenReturn(m);
        p=createProject("test");
        p2=createProject("test2");
        t=createTicket(m,p,null);
        createTicket(m2,p2,"p2");

        String startTime=LocalDate.now().atStartOfDay().format(DateTimeMapUtil.formatByDot);
        String endTime=LocalDate.now().atStartOfDay().plusDays(1L).format(DateTimeMapUtil.formatByDot);
        String startTime2=LocalDate.now().plusMonths(1L).atStartOfDay().format(DateTimeMapUtil.formatByDot);
        String endTime2=LocalDate.now().plusMonths(1L).plusDays(1L).atStartOfDay().format(DateTimeMapUtil.formatByDot);
        monthCurrent=LocalDate.now().format(DateTimeMapUtil.formatByDot2);
        monthFuture=LocalDate.now().plusMonths(1L).format(DateTimeMapUtil.formatByDot2);

        for(int i=0;5>i;i++) {
            projectCertService.createMeet(new MeetDto(startTime,endTime),p.getId());
            projectCertService.createMeet(new MeetDto(startTime2,endTime2),p.getId());
        }
        for(int i=0;5>i;i++) {
            projectCertService.createMeet(new MeetDto(startTime,endTime),p2.getId());
        }
    }

    @Test
    @DisplayName("해당되는 project를 잘가져오는가.")
    void callProjectsTest(){


        Mockito.when(securityMemberReadService.securityMemberRead())
                .thenReturn(m);

        List<ProjectDto> projectDtoList=projectCertService.getProejctList();
        assertThat(projectDtoList.size()).isEqualTo(1);
    }
    @Test
    @DisplayName("해당되는 meeting 내역을 잘가져오는가.")
    void callMeetTest() {


        Mockito.when(securityMemberReadService.securityMemberRead())
                .thenReturn(m);

        List<MeetDateDto> meetDateDtos = meetService.getMeetList(monthCurrent);
        assertThat(meetDateDtos.size()).isEqualTo(5);

        List<MeetDateDto> meetDateDtos2 = meetService.getMeetList(monthFuture);
        assertThat(meetDateDtos2.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("ticket alias 변경 체크")
    void noAuthTest(){

        Mockito.when(securityMemberReadService.securityMemberRead())
                .thenReturn(m);


        projectCertService.changeAlias("change",p.getId());
        Ticket t2=ticketRepository.findById(t.getId()).get();
        assertThat(t2.getAlias()).isEqualTo("change");
    }

    @Test
    @DisplayName("alias 미지정시 날라오는 이름체크")
    void aliasTest(){

        Mockito.when(securityMemberReadService.securityMemberRead())
                .thenReturn(m);
        List<Meet> meets=meetRepository.findAll();
        MeetConnectDto meetConnectDto=meetService.checkIn(meets.getFirst().getId());
        assertThat(meetConnectDto.getAlias()).isEqualTo("test1");
    }
}
