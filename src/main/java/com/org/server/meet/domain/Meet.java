package com.org.server.meet.domain;


import com.org.server.project.domain.Project;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.org.server.util.BaseTime;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity

public class Meet extends BaseTime{


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "projectId")
    private Project project;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String meetName;
    private Boolean deleted=false;


    @Builder
    public Meet(Project project, LocalDateTime startTime, String meetName,LocalDateTime endTime) {
        this.project = project;
        this.startTime = startTime;
        this.meetName=meetName;
        this.endTime=endTime;
    }
    public void updateDeleted(){
        this.deleted=!this.deleted;
    }
}
