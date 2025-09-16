package com.org.server.chat.domain;

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
@Entity
public class ChatRoom extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable=false, length=16)
	private ChatType  chatType;   // PROJECT / MEET

	@Column(nullable=false)
	private Long refId;        // projectId 또는 meetId

	@Builder
	private ChatRoom(ChatType  chatType, Long refId) {
		this.chatType = chatType;
		this.refId = refId;
	}

	public static ChatRoom of(ChatType  chatType, long refId) {
		return ChatRoom.builder().chatType(chatType).refId(refId).build();
	}

}
