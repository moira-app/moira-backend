package com.org.server.chat.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.org.server.util.BaseTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "chat_room")
public class ChatRoom extends BaseTime {

	@Id
	private Long id;

	private ChatType  chatType;   // PROJECT / MEET
	private Long refId;        // projectId 또는 meetId


	@Builder
	private ChatRoom(Long id,ChatType  chatType, Long refId) {
		this.id = id;
		this.chatType = chatType;
		this.refId = refId;
	}


	public static ChatRoom of(ChatType  chatType, long refId) {
		return ChatRoom.builder().chatType(chatType).refId(refId).build();
	}

}
