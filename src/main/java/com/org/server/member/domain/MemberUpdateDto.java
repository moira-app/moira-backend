package com.org.server.member.domain;


import com.org.server.member.GenderType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateDto {
    @NotBlank(message = "닉네임은 빌수없습니다.")
    @Size(min = 4,max=12,message = "닉네임은 4~12자입니다")
    @Schema(description = "만약 업데이트를 하지않는 다면은 기존과 같은 닉네임을 주세요")
    private String nickName;
    @Schema(description = "만약 업데이트를 하지않는 다면은 비워주세요")
    private String password;
}
