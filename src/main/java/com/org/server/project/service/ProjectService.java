package com.org.server.project.service;


import com.org.server.member.service.SecurityMemberReadService;
import com.org.server.project.domain.Project;
import com.org.server.project.domain.ProjectDto;
import com.org.server.project.repository.ProjectAdvanceRepo;
import com.org.server.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.org.server.member.domain.Member;



@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final SecurityMemberReadService securityMemberReadService;
    private final ProjectAdvanceRepo projectAdvanceRepo;
    public void createProject(String title){
        Project project=new Project(title);
        projectRepository.save(project);
    }

    public List<ProjectDto> getProjectList(){
        Member m=securityMemberReadService.securityMemberRead();
        return projectAdvanceRepo.getProjectList(m);
    }

}
