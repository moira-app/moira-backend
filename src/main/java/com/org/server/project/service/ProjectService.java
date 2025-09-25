package com.org.server.project.service;



import com.org.server.exception.MoiraException;
import com.org.server.project.domain.Project;
import com.org.server.project.repository.ProjectRepository;
import com.org.server.whiteBoardAndPage.domain.WhiteBoard;
import com.org.server.whiteBoardAndPage.repository.WhiteBoardRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final WhiteBoardRepo whiteBoardRepo;


    public Boolean checkProject(Long id){
        Optional<Project> p=projectRepository.findById(id);
        if(p.isEmpty()||p.get().getDeleted()){
            return false;
        }
        return true;
    }
    public void createProject(String title){
        Project project=new Project(title);
        project=projectRepository.save(project);
        WhiteBoard whiteBoard= WhiteBoard.builder()
                .project(project)
                .build();
        whiteBoardRepo.save(whiteBoard);
    }
    public void delProject(Long id){
        Optional<Project> p=projectRepository.findById(id);
        if(p.isEmpty()||p.get().getDeleted()){
            throw new MoiraException("없는 프로젝트입니다", HttpStatus.BAD_REQUEST);
        }
        p.get().updateDeleted();
    }



}
