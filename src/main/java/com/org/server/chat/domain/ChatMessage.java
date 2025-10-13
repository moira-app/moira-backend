package com.org.server.chat.domain;

import com.org.server.util.BaseTime;
import com.sun.jdi.CharType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ChatMessage extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 채팅 타입 ( PROJECT, MEET )
	@Enumerated(EnumType.STRING)
	private ChatType chatType;

	// 방 Id ( 프로젝트Id, 미트Id )
	private Long roomId;

	// 사용자Id
	private Long senderId;

	// 내용
	private String content;


	@Builder
	private ChatMessage(ChatType chatType, Long roomId, Long senderId, String content) {
		this.chatType = chatType;
		this.roomId = roomId;
		this.senderId = senderId;
		this.content = content;
	}

	public static ChatMessage of(ChatType chatType, long roomId, long senderId, String content) {
		return ChatMessage.builder()
			.chatType(chatType)
			.roomId(roomId)
			.senderId(senderId)
			.content(content)
			.build();
	}


}
