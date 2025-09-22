package com.org.server.project.service;



import com.org.server.project.domain.Project;
import com.org.server.project.repository.ProjectRepository;
import com.org.server.whiteBoardAndPage.domain.WhiteBoard;
import com.org.server.whiteBoardAndPage.repository.WhiteBoardRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final WhiteBoardRepo whiteBoardRepo;

    public void createProject(String title){
        Project project=new Project(title);
        project=projectRepository.save(project);
        WhiteBoard whiteBoard= WhiteBoard.builder()
                .project(project)
                .build();
        whiteBoardRepo.save(whiteBoard);
    }



}
