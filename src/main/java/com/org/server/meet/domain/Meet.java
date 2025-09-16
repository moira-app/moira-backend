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
    private String meetUrl;


    @Builder
    public Meet(Project project, LocalDateTime startTime, String meetUrl) {
        this.project = project;
        this.startTime = startTime;
        this.meetUrl = meetUrl;
    }
}
