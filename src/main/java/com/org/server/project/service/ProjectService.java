package com.org.server.project.service;



import com.org.server.project.domain.Project;
import com.org.server.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;

    public void createProject(String title){
        Project project=new Project(title);
        projectRepository.save(project);
    }



}
