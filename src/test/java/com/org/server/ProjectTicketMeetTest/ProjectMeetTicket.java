package com.org.server.ProjectTicketMeetTest;


import com.org.server.certification.domain.AliasDto;
import com.org.server.chat.domain.ChatRoom;
import com.org.server.exception.MoiraException;
import com.org.server.meet.domain.*;
import com.org.server.project.domain.Project;

import com.org.server.project.domain.ProjectInfoDto;
import com.org.server.support.IntegralTestEnv;
import com.org.server.ticket.domain.Master;
import com.org.server.ticket.domain.Ticket;
import com.org.server.util.DateTimeMapUtil;
import com.org.server.websocket.domain.AlertMessageDto;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProjectMeetTicket extends IntegralTestEnv {


    Project p;

    Project p2;

    ChatRoom c1;
    ChatRoom c2;

    Ticket t;

    Member m;

    Member m2;

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
        m2=createMember(2L);
        Mockito.when(securityMemberReadService.securityMemberRead())
                .thenReturn(m);
        p=createProject("test","Dfdfd");
        p2=createProject("test2","fsdfd");
        c1=createChatRoom(p);
        c2=createChatRoom(p2);
        t=createTicket(m,p,null, Master.MASTER);
        createTicket(m2,p2,"p2",Master.MASTER);

        String startTime=DateTimeMapUtil.parseServerTimeToClientFormat(LocalDate.now().atStartOfDay());
        String endTime=DateTimeMapUtil.parseServerTimeToClientFormat(LocalDate.now().atStartOfDay().plusDays(1L));
        String startTime2=DateTimeMapUtil.parseServerTimeToClientFormat(LocalDate.now().plusMonths(1L).atStartOfDay());
        String endTime2=DateTimeMapUtil.parseServerTimeToClientFormat(LocalDate.now().plusMonths(1L).plusDays(1L).atStartOfDay());
        monthCurrent=DateTimeMapUtil.parseServerTimeToClientFormat(LocalDate.now().atStartOfDay());
        monthFuture=DateTimeMapUtil.parseServerTimeToClientFormat(LocalDate.now().plusMonths(1L).atStartOfDay());

        for(int i=0;5>i;i++) {
            projectCertService.createMeet(new MeetCreateDto("Test",startTime,endTime),p.getId());
            projectCertService.createMeet(new MeetCreateDto("test",startTime2,endTime2),p2.getId());
        }
        Mockito.reset(alertEventListener);
    }



    @Test
    @DisplayName("회원별 해당되는 project를 잘가져오는가.")
    void callProjectsTest(){
        Mockito.when(securityMemberReadService.securityMemberRead())
                .thenReturn(m);
        List<ProjectInfoDto> projectDtoList=projectCertService.getProejctList();
        assertThat(projectDtoList.size()).isEqualTo(1);
    }


    @Test
    @DisplayName("회원별 해당되는 meeting 내역을 잘가져오는가.")
    void callMeetTest() {
        Mockito.when(securityMemberReadService.securityMemberRead())
                .thenReturn(m);
        MeetListDto meetListDto=new MeetListDto(monthCurrent);
        MeetListDto meetListDtoFuture=new MeetListDto(monthFuture);
        List<MeetDateDto> meetDateDtos = meetService.getMeetList(meetListDto,p.getId());
        assertThat(meetDateDtos.size()).isEqualTo(5);

        List<MeetDateDto> meetDateDtos2 = meetService.getMeetList(meetListDtoFuture,p2.getId());
        assertThat(meetDateDtos2.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("ticket alias 변경 체크")
    void noAuthTest(){
        Mockito.when(securityMemberReadService.securityMemberRead())
                .thenReturn(m);
        Mockito.doNothing()
                        .when(alertEventListener)
                                .alertMessage(Mockito.any(AlertMessageDto.class));
        projectCertService.changeAlias("change",p.getId());
        Mockito.verify(alertEventListener).alertMessage(Mockito.any(AlertMessageDto.class));

        Ticket t2=ticketRepository.findById(t.getId()).get();
        assertThat(t2.getAlias()).isEqualTo("change");
        List<Meet> meets=meetRepository.findAll();

        MeetEnterDto meetEnterDto=new MeetEnterDto(meets.getFirst().getId(),DateTimeMapUtil.parseServerTimeToClientFormat(LocalDateTime.now()));

        MeetConnectDto meetConnectDto=projectCertService.checkInMeet(meetEnterDto,p.getId());
        assertThat(meetConnectDto.getAlias()).isEqualTo("change");
    }

    @Test
    @DisplayName("alias 미지정시 날라오는 이름체크")
    void aliasTest(){

        Mockito.when(securityMemberReadService.securityMemberRead())
                .thenReturn(m);
        List<Meet> meets=meetRepository.findAll();

        MeetEnterDto meetEnterDto=new MeetEnterDto(meets.getFirst().getId(),DateTimeMapUtil.parseServerTimeToClientFormat(LocalDateTime.now()));
        MeetConnectDto meetConnectDto=projectCertService.checkInMeet(meetEnterDto,
                p.getId());
        assertThat(meetConnectDto.getAlias()).isEqualTo("test1");
    }

    @Test
    @DisplayName("프로젝트,티켓,회의 삭제 테스트")
    void delTest(){
        Mockito.when(securityMemberReadService.securityMemberRead())
                .thenReturn(m);
        Mockito.doNothing()
                        .when(alertEventListener)
                                .alertMessage(Mockito.any(AlertMessageDto.class));
        MeetListDto meetListDto=new MeetListDto(monthCurrent);
        List<MeetDateDto> meetList=meetService.getMeetList(meetListDto,p.getId());
        projectCertService.delMeet(meetList.getFirst().getMeetId(),p.getId());

        List<MeetDateDto> meetDateDtos = meetService.getMeetList(meetListDto,p.getId());
        assertThat(meetDateDtos.size()).isEqualTo(4);


        projectCertService.delProject(p.getId());

        Project project=projectRepository.findById(p.getId()).get();
        Mockito.verify(alertEventListener,Mockito.times(2))
                .alertMessage(Mockito.any(AlertMessageDto.class));
        assertThat(project.getDeleted()).isTrue();
    }

    @Test
    @DisplayName("마스터 권한 테스트")
    void testingMaster(){
        Mockito.when(securityMemberReadService.securityMemberRead())
                .thenReturn(m);
        Mockito.doNothing()
                        .when(alertEventListener)
                                .alertMessage(Mockito.any(AlertMessageDto.class));

        assertThrows(MoiraException.class,()->projectCertService.delTicket(p.getId(),m2.getId()));
        assertThrows(MoiraException.class,()->projectCertService.delProject(p2.getId()));
        ticketRepository.save(createTicket(m2,p,null, Master.ELSE));
        projectCertService.delTicket(p.getId(),m2.getId());
        Mockito.verify(alertEventListener,Mockito.times(2)).alertMessage(Mockito.any(AlertMessageDto.class));
        assertThat(ticketRepository.findByMemberIdAndProjectId(m2.getId(),p.getId()).get().getMaster())
                .isEqualTo(Master.MASTER);
    }
    @Test
    @DisplayName("티켓 발급 삭제 테스트")
    void ticketProvideTest(){
        AliasDto aliasDto =new AliasDto("testing");
        Mockito.when(securityMemberReadService.securityMemberRead())
                        .thenReturn(m);
        Mockito.doNothing()
                .when(alertEventListener)
                        .alertMessage(Mockito.any(AlertMessageDto.class));

        assertThatThrownBy(()->{
            projectCertService.createTicket(p.getProjectUrl(),aliasDto);}
        )
                .isInstanceOf(MoiraException.class)
                .hasMessage("이미 초대되었거나 혹은 퇴출된 유저입니다");

        assertThatThrownBy(()->{projectCertService.delTicket(t.getProjectId(),null);})
                .isInstanceOf(MoiraException.class)
                        .hasMessage("마스터 권한은 탈퇴시 다음 마스터를 지정해야됩니다");

        assertThat(ticketRepository.findByMemberIdAndProjectId(m.getId(),t.getProjectId()).get().getDeleted())
                .isFalse();

        assertThatThrownBy(()->{
            projectCertService.createTicket(p.getProjectUrl(),aliasDto);}
        )
                .isInstanceOf(MoiraException.class)
                .hasMessage("이미 초대되었거나 혹은 퇴출된 유저입니다");

        projectCertService.createTicket(p2.getProjectUrl(),aliasDto);
        Mockito.when(securityMemberReadService.securityMemberRead())
                .thenReturn(m2);
        assertThatThrownBy(()->{projectCertService.delTicket(p2.getId(), m2.getId());})
                .isInstanceOf(MoiraException.class)
                .hasMessage("마스터 권한은 탈퇴시 다음 마스터를 지정해야됩니다");
        projectCertService.delTicket(p2.getId(),m.getId());

        Mockito.verify(alertEventListener,Mockito.times(3)).alertMessage(Mockito.any(AlertMessageDto.class));
        Optional<Ticket> tNew=ticketRepository.findByMemberIdAndProjectId(m.getId(),p2.getId());
        assertThat(tNew.isPresent()).isTrue();
        assertThat(tNew.get().getMaster()).isEqualTo(Master.MASTER);

    }
}
