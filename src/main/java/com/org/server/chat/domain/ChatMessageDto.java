package com.org.server.chat.domain;
import java.time.LocalDateTime;

public record ChatMessageDto(
	Long id,
	ChatType scope,
	Long roomId,
	Long senderId,
	String content
) {}