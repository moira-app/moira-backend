package com.org.server.projecT;

import static org.assertj.core.api.Assertions.*;

import com.org.server.project.domain.Project;
import com.org.server.support.IntegralTestEnv;
import com.org.server.whiteBoardAndPage.domain.Page;
import com.org.server.whiteBoardAndPage.domain.PageDto;
import com.org.server.whiteBoardAndPage.domain.WhiteBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;

public class ProjectTtest extends IntegralTestEnv {

    Project p;
    WhiteBoard w;
    Page page;


    @BeforeEach
    void setBeforeTest(){
        p=createProject("test");
        w=createWhiteBoard(p);
        page=createPage(w.getId());
    }


    @Test
    @DisplayName("project 생성 및 화이트 보드 생성테스트")
    void testGenProjectAndWhiteBoard(){
        projectService.createProject("sdfddf");
        projectPageS3Service.savePage(p.getId(),null,null,"Dfsdfsd");


        List<WhiteBoard> whiteBoardList=whiteBoardRepo.findAll();
        List<Project> projectList=projectRepository.findAll();
        List<Page> pageList=pageRepo.findAll();
        assertThat(whiteBoardList.size()).isEqualTo(2L);
        assertThat(projectList.size()).isEqualTo(2L);
        assertThat(pageList.size()).isEqualTo(2L);
    }

    @Test
    @DisplayName("해당 프로젝트와 관련된 페이지 리스트 조회 테스트 및 삭제 판정 테스트")
    void testGetPageList(){

        List<PageDto> pageDtos=projectPageS3Service.getPageList(p.getId());
        assertThat(pageDtos.size()).isEqualTo(1L);

        projectPageS3Service.delPage(page.getId());

        pageDtos=projectPageS3Service.getPageList(p.getId());
        assertThat(pageDtos.size()).isEqualTo(0L);

    }

    @Test
    @DisplayName("해당 페이지로부터 get url 생성여부")
    void testGetUrl(){
        page.updateFileLocation("Dfdsfsdf");
        page=pageRepo.save(page);
        String url=projectPageS3Service.getPageUrl(page.getId());

        assertThat(url==null).isFalse();
    }

    @Test
    @DisplayName("해당 페이지에대한 update url 발급 테스트" +
            "및 페이지 최초 저장,여러번 저장시 filelocation 변동 및 filename 변동 테스트")
    void testUpdateUrl(){
        String url=projectPageS3Service.updatePageUrl(page.getId());
        assertThat(url==null).isFalse();
        Page page1=pageRepo.findById(page.getId()).get();

        assertThat(page1.getFileLocation()==null).isTrue();

        projectPageS3Service.savePage(p.getId(),page.getId(),
                url,"chageName");
        Page page2=pageRepo.findById(page.getId()).get();
        assertThat(page2.getFileLocation()!=null).isTrue();
        assertThat(page2.getPageName()!=page1.getPageName()).isTrue();

        projectPageS3Service.savePage(p.getId(),page.getId(),
                "dsfdsfdsf","chageName");

        Page page3=pageRepo.findById(page.getId()).get();
        assertThat(page3.getFileLocation().equals(url)).isTrue();
        assertThat(page2.getPageName().equals(page3.getPageName())).isTrue();
    }
}
