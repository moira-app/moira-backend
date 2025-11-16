package com.org.server.chat.domain;

public record ChatRoomDto(
	Long id,
	ChatType chatType,
	Long refId
) {}