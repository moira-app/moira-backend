package com.org.server.chat.repository;

import com.org.server.chat.domain.ChatMessage;
import com.org.server.chat.domain.QChatMessage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatMessageAdvanceRepository {

	private final JPAQueryFactory queryFactory;
	private final EntityManager em;

	private static final QChatMessage m = QChatMessage.chatMessage;

	/** (Page) roomId 기준 최신순 페이지 조회 — total count 포함 */
	public Page<ChatMessage> findPageByRoomId(Long roomId, Pageable pageable) {
		List<ChatMessage> content = queryFactory
			.selectFrom(m)
			.where(m.roomId.eq(roomId))
			.orderBy(m.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// total count (필요 없으면 Slice 버전 사용 권장)
		var countQuery = queryFactory
			.select(m.id.count())
			.from(m)
			.where(m.roomId.eq(roomId));

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	/** (Slice) roomId 기준 최신순 슬라이스 — count 없이 다음 페이지 여부만 판단 */
	public Slice<ChatMessage> findSliceByRoomId(Long roomId, Pageable pageable) {
		List<ChatMessage> content = queryFactory
			.selectFrom(m)
			.where(m.roomId.eq(roomId))
			.orderBy(m.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1) // hasNext 판단용
			.fetch();

		boolean hasNext = content.size() > pageable.getPageSize();
		if (hasNext) content.remove(pageable.getPageSize());

		return new SliceImpl<>(content, pageable, hasNext);
	}

	/** 최신 메시지 1개 (id desc) */
	public Optional<ChatMessage> findLatestByRoomId(Long roomId) {
		ChatMessage one = queryFactory
			.selectFrom(m)
			.where(m.roomId.eq(roomId))
			.orderBy(m.id.desc())
			.limit(1)
			.fetchOne();
		return Optional.ofNullable(one);
	}
}