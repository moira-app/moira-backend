package com.org.server.chat.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoomMember {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;

	@Column(nullable = false)
	long roomId;

	@Column(nullable = false)
	long ticketId;


	@Builder
	private ChatRoomMember(long roomId, long ticketId) {
		this.roomId = roomId;
		this.ticketId = ticketId;
	}
}
