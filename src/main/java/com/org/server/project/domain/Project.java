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
    private String imgUrl;


    public Project(String title,String projectUrl,String imgUrl) {
        this.title = title;
        this.projectUrl=projectUrl;
        this.imgUrl=imgUrl;
    }
    public void updateDeleted(){
        this.deleted=!this.deleted;
    }
    public void updateImgUrl(String imgUrl){
        this.imgUrl=imgUrl;
    }
}
