package com.org.server.chat.domain;

public record ChatRoomMemberDto(
	Long id,
	Long roomId,
	Long memberId
) { }
