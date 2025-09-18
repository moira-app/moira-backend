package com.org.server.project.repository;

import com.org.server.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProjectRepository extends JpaRepository<Project,Long>{
}
