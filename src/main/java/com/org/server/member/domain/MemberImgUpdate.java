package com.org.server.member.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberImgUpdate {
    @NotNull
    @Schema(description = "필수값입니다")
    private Long id;
    @NotNull
    @Schema(description = "필수값입니다.내 프로필 이미지를 교체해서 저장을 원할 경우에는 파일 이름값을" +
            "서버로부터 받은 url로 저장이 완료되고 서버에 최종적으로 바뀐 이미지의 주소를 저장할떄에는" +
            "이미지 url값을 주세요 즉 서버로부터 받은 url을 주세요.")
    private String fileName;
}
