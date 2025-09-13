package com.org.server.member.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignInDto {

	@NotBlank(message = "이메일은 비거나 공백일수없습니다.")
	private String email;
	@NotNull(message = "닉네임은 최대 12글자 최소 3글자입니다")
	@Size(min = 3,max = 12)
	private String nickName;
	@NotBlank(message = "비밀번호는 공백,비는게 안됩니다")
	private String password;


}
