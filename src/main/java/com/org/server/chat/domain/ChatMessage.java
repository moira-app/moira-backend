package com.org.server.chat.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.org.server.util.BaseTime;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Document(collection = "chat_message")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@CompoundIndexes({
	// 방별 최근 메시지 페이징 최적화
	@CompoundIndex(name = "room_time_idx", def = "{ 'chatType': 1, 'roomId': 1, 'createdAt': -1 }")
})
public class ChatMessage extends BaseTime {

	@Id
	private Long id;

	// 채팅 타입 ( PROJECT, MEET )
	private ChatType chatType;

	// 방 Id ( 프로젝트Id, 미트Id )
	private Long roomId;

	// 사용자Id
	private Long senderId;

	// 내용
	private String content;


	@Builder
	private ChatMessage(Long id, ChatType chatType, Long roomId, Long senderId, String content) {
		this.id = id;
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
