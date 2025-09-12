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
    @NotNull(message = "출생일은 비면안됩니다.")
    @Max(value = 31,message = "최대 31일까지입니다.")
    @Min(value =1,message = "최소 1일입니다")
    private int birthDay;
    @NotNull(message = "출생월은 비면안됩니다")
    @Max(value = 12,message = "최대 12월입니다")
    @Min(value =1,message = "최소 1월입니다.")
    private int birthMonth;
    @NotNull(message = "출생년도는 비면안됩니다.")
    @Min(value =1899)
    @Max(value = 3000)
    private int birthYear;
    private GenderType genderType;
}
