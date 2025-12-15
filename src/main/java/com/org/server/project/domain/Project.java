package com.org.server.project.domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import com.org.server.util.BaseTime;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Project extends BaseTime{


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String title;
    private Boolean deleted=false;
    private String projectUrl;
    private LocalDateTime createDate;


    public Project(String title,String projectUrl,LocalDateTime createDate) {
        this.title = title;
        this.projectUrl=projectUrl;
        this.createDate=createDate;
    }
    public void updateDeleted(){
        this.deleted=!this.deleted;
    }
}
