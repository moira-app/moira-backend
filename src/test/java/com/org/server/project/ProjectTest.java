package com.org.server.project;

import com.org.server.chat.domain.ChatType;
import com.org.server.meet.domain.Meet;
import com.org.server.member.domain.Member;
import com.org.server.project.domain.Project;
import com.org.server.support.IntegralTestEnv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectTest extends IntegralTestEnv {

    Project p;


    @BeforeEach
    void setBeforeTest() {
        p = createProject("test","Dfsff");
    }


    @Test
    @DisplayName("project 생성 테스트")
    void testGenProjectAndWhiteBoard() {

        Member member=createMember(1L);
        Mockito.when(securityMemberReadService.securityMemberRead())
                .thenReturn(member);
        projectService.createProject("sdfddf",LocalDateTime.now().toString());
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList.size()).isEqualTo(2L);
        assertThat( chatRoomRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("meet 생성 테스트")
    void testGenMeet(){

        meetService.saveMeet(Meet.builder()
                        .meetName("dfds")
                        .project(p)
                        .startTime(LocalDateTime.now())
                .build());

        assertThat(meetRepository.findAll().size()).isEqualTo(1);
        assertThat( chatRoomRepository.findAll().size()).isEqualTo(1);
    }

}