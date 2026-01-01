package com.org.server.member.domain;

import io.swagger.v3.oas.annotations.media.Schema;
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
	@Schema(description = "이메일은 비면안됩니다.")
	private String email;
	@NotBlank(message = "닉네임은 최대 12글자 최소 3글자입니다")
	@Size(min = 3,max = 12)
	@Schema(description = "닉네임은 비거나면 안되고 최소 3글자 에서 최대 12글자입니다.")
	private String nickName;
	@NotBlank(message = "비밀번호는 공백,비는게 안됩니다")
	@Schema(description = "비밀번호는 공백,비는게 안됩니다.")
	private String password;

}
