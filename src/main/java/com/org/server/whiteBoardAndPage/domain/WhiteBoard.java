package com.org.server.whiteBoardAndPage.domain;


import com.org.server.project.domain.Project;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.org.server.util.BaseTime;

@Entity
@Getter
@NoArgsConstructor
public class WhiteBoard extends BaseTime{



    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    private Project project;

    @Builder
    public WhiteBoard(Project project) {
        this.project=project;
    }
}
