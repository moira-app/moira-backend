package com.org.server.member.domain;

import com.org.server.member.GenderType;
import com.org.server.member.MemberType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberDto {

    private Long id;
    private String nickName;
    private String email;
    private GenderType genderType;
    private int birthDay;
    private int birthMonth;
    private int birthYear;


    public static MemberDto createMemberDto(Member member){

        return MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .genderType(member.getGenderType())
                .nickName(member.getNickName())
                .birthDay(member.getBirthDay())
                .birthYear(member.getBirthYear())
                .birthMonth(member.getBirthMonth())
                .build();
    }

    @Builder
    public MemberDto(Long id, String nickName, String email, GenderType genderType,
                     int birthDay, int birthMonth, int birthYear) {
        this.id = id;
        this.nickName = nickName;
        this.email = email;
        this.genderType = genderType;
        this.birthDay = birthDay;
        this.birthMonth = birthMonth;
        this.birthYear = birthYear;
    }
}
