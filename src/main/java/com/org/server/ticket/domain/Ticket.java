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

    private Long projectId;

    private Long memberId;

    private String alias;

    @Builder
    public Ticket(Long projectId,Long memberId, String alias) {
        this.projectId=projectId;
        this.memberId=memberId;
        this.alias = alias;
    }

    public void updateAlias(String alias){
        this.alias=alias;
    }
}
