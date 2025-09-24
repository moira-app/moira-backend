package com.org.server.project.domain;


import jakarta.persistence.*;
import lombok.Getter;
import com.org.server.util.BaseTime;
import lombok.NoArgsConstructor;

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
    public Project(String title) {
        this.title = title;
    }
    public void updateDeleted(){
        this.deleted=!this.deleted;
    }
}
