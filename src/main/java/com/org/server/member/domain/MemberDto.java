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



    public static MemberDto createMemberDto(Member member){

        return MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())

                .nickName(member.getNickName())
                .build();
    }

    @Builder
    public MemberDto(Long id, String nickName, String email) {
        this.id = id;
        this.nickName = nickName;
        this.email = email;

    }
}
