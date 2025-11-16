package com.org.server.chat.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.org.server.chat.domain.QChatRoomMember;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChatRoomMemberAdvanceRepository {

	private final JPAQueryFactory queryFactory;
	private final EntityManager em;

	private final QChatRoomMember chatRoomMember = QChatRoomMember.chatRoomMember;


	public long saveNew(Long roomId, Long ticketId) {
		return queryFactory
			.insert(chatRoomMember)
			.columns(
				chatRoomMember.roomId,
				chatRoomMember.ticketId
			)
			.values(roomId, ticketId)
			.execute();

	}

	public List<Long> findTicketIdsByRoomId(Long roomId) {
		return queryFactory
			.select(chatRoomMember.ticketId)
			.from(chatRoomMember)
			.where(chatRoomMember.roomId.eq(roomId))
			.fetch();
	}
}
