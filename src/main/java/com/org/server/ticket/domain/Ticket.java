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
    
    //1:다
    private Long projectId;

    //1:다
    private Long memberId;

    private String alias;

    private Boolean deleted=false;

    @Enumerated(EnumType.STRING)
    private Master master;

    @Builder
    public Ticket(Long projectId,Long memberId, String alias,Master master) {
        this.projectId=projectId;
        this.memberId=memberId;
        this.alias = alias;
        this.master=master;
    }

    public void updateAlias(String alias){
        this.alias=alias;
    }

    public void updateDeleted(){
        this.deleted=!this.deleted;
    }

    public void updateMaster(Master master){this.master=master;}
}
