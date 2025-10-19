package com.org.server.chat.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Document(collection = "chat_room_member")
public class ChatRoomMember {

	@Id
	long id;

	long roomId;
	long ticketId;


	@Builder
	private ChatRoomMember(long roomId, long ticketId) {
		this.roomId = roomId;
		this.ticketId = ticketId;
	}
}
