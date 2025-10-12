package com.org.server.project.repository;

import com.org.server.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project,Long>{
    Optional<Project> findByProjectUrl(String url);
}
