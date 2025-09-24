package com.org.server.member.domain;


import com.org.server.member.GenderType;
import com.org.server.member.MemberType;
import com.org.server.util.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String email;
    private String nickName;
    private String password;
    private MemberType memberType;
    private Boolean deleted=false;


    @Builder
    public Member(Long id, String email, String nickName, String password,
                  MemberType memberType) {
        this.id = id;
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.memberType = memberType;
    }
    public void updatePassword(String password){
        this.password=password;
    }

    public void updateNickName(String nickName){
        this.nickName=nickName;
    }

    public void updateDeleted(){
        this.deleted=!this.deleted;
    }
}
