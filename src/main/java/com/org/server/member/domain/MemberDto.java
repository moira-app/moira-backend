package com.org.server.member.domain;

import com.org.server.member.GenderType;
import com.org.server.member.MemberType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberDto {


    @Schema(description = "회원 식별 아이디값입니다.")
    private Long id;
    @Schema(description = "회원 닉네임입니다")
    private String nickName;
    @Schema(description = "회원 이메일입니다.")
    private String email;
    private String imgUrl;

    public static MemberDto createMemberDto(Member member){
        return MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickName(member.getNickName())
                .imgUrl(member.getImgUrl()==null ? "defaulturl":member.getImgUrl())
                .build();
    }

    @Builder
    public MemberDto(Long id, String nickName, String email,String imgUrl) {
        this.id = id;
        this.nickName = nickName;
        this.email = email;
        this.imgUrl=imgUrl;

    }
}
