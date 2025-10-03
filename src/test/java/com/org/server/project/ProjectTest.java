package com.org.server.project;

import com.org.server.project.domain.Project;
import com.org.server.support.IntegralTestEnv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        projectService.createProject("sdfddf");
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList.size()).isEqualTo(2L);
    }

}