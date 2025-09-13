package com.org.server.member.domain;


import com.org.server.member.GenderType;
import com.org.server.member.MemberType;
import com.org.server.util.BaseTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.criteria.CriteriaBuilder;
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
    private GenderType genderType;
    private Integer birthYear;
    private Integer birthDay;
    private Integer birthMonth;

    @Builder
    public Member(Long id, String email, String nickName, String password,
                  MemberType memberType, GenderType genderType, Integer birthYear, Integer birthDay,
                  Integer birthMonth) {
        this.id = id;
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.memberType = memberType;
        this.genderType = genderType;
        this.birthYear = birthYear;
        this.birthDay = birthDay;
        this.birthMonth = birthMonth;
    }
    public void updatePassword(String password){
        this.password=password;
    }
    public void updateBirth(Integer birthDay,Integer birthMonth,Integer birthYear){
        if (this.birthDay==null||!this.birthDay .equals(birthDay)) {
            this.birthDay = birthDay;
        }
        if (this.birthMonth==null||!this.birthMonth .equals( birthMonth)) {
            this.birthMonth = birthMonth;
        }
        if (this.birthYear==null||!this.birthYear.equals (birthYear)){
            this.birthYear = birthYear;
        }
    }
    public void updateGenderType(GenderType genderType){
        this.genderType=genderType;
    }
    public void updateNickName(String nickName){
        this.nickName=nickName;
    }
}
