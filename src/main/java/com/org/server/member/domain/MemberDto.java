package com.org.server.member.domain;

import lombok.Data;

@Data
public class MemberDto {


	private Long id;
	private String email;
	private String nickName;
	private String password;


}
