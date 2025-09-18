package com.org.server.ticket.domain;


import com.org.server.project.domain.Project;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.org.server.util.BaseTime;
import com.org.server.member.domain.Member;

@Getter
@NoArgsConstructor
@Entity
@Table(uniqueConstraints =  @UniqueConstraint(columnNames = {"memberId", "projectId"}))
public class Ticket extends BaseTime{


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    private String alias;

    @Builder
    public Ticket(Project project, Member member, String alias) {
        this.project = project;
        this.member = member;
        this.alias = alias;
    }

    public void updateAlias(String alias){
        this.alias=alias;
    }
}
