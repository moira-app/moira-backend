package com.org.server.member.domain;


import com.org.server.member.GenderType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateDto {
    @NotNull(message = "id값은 있어야됩니다.")
    private Long id;
    @NotBlank(message = "닉네임은 빌수없습니다.")
    @Size(min = 4,max=12,message = "닉네임은 4~12자입니다")
    private String nickName;
    private String password;

}
