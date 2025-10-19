package com.org.server.chat.repository.impl;

import com.org.server.chat.domain.ChatMessage;
import com.org.server.chat.repository.ChatMessageRepository;
import com.org.server.chat.service.SequenceService;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.support.PageableExecutionUtils;
import static org.springframework.data.domain.Sort.Direction.DESC;

import java.util.List;
import java.util.Optional;

// @Profile("mongo")
@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepository {

	private final MongoTemplate mongoTemplate;
	private final SequenceService seq;

	/** (Page) roomId 기준 최신순 페이지 조회 — total count 포함 */
	public Page<ChatMessage> findPageByRoomId(Long roomId, Pageable pageable) {
		Query query = new Query( Criteria.where("roomId").is(roomId) );

		Sort sort = Sort.by(DESC, "createDate").and(Sort.by(DESC, "id"));

		Query q = query.with(sort).with(pageable);
		List<ChatMessage> chatMessageList = mongoTemplate.find(q, ChatMessage.class);

		return PageableExecutionUtils.getPage(
			chatMessageList,
			pageable,
			() -> mongoTemplate.count(query, ChatMessage.class)
		);

	}

	/**symotion-jumptoanywhere)  (Slice) roomId 기준 최신순 슬라이스 — count 없이 다음 페이지 여부만 판단 */
	public Slice<ChatMessage> findSliceByRoomId(Long roomId, Pageable pageable) {
		Query query = new Query( Criteria.where("roomId").is(roomId) );
		Sort sort = Sort.by(DESC, "createDate").and(Sort.by(DESC, "id"));

		Query q	 = query.with(sort)
			.skip(pageable.getOffset())
			.limit(pageable.getPageSize() + 1); // 다음 페이지 존재 여부 확인

		List<ChatMessage> chatMessageList = mongoTemplate.find(q, ChatMessage.class);

		boolean hasNext = chatMessageList.size() > pageable.getPageSize();
		if (hasNext) chatMessageList = chatMessageList.subList(0, pageable.getPageSize());

		return new SliceImpl<>(chatMessageList, pageable, hasNext);
	}

	/** 최신 메시지 1개 (id desc) */
	public Optional<ChatMessage> findLatestByRoomId(Long roomId) {

		Query query = new Query(
			Criteria
				.where("roomId").is(roomId))
				.with(Sort.by(DESC, "roomId")
				.and(Sort.by(DESC, "id")))
			.limit(1);

		ChatMessage one = mongoTemplate.findOne(query, ChatMessage.class);

		return Optional.ofNullable(one);
	}

	@Override
	public Optional<ChatMessage> findTopByRoomIdOrderByIdDesc(Long roomId) {
		Query query = new Query(
			Criteria
				.where("roomId").is(roomId))
			.with(Sort.by(DESC, "id"))
			.limit(1);

		ChatMessage one = mongoTemplate.findOne(query, ChatMessage.class);

		return Optional.ofNullable(one);
	}

	@Override
	public ChatMessage save(ChatMessage chatMessage) {
		if(chatMessage.getId() == null){
			long newId = seq.next("chat_message");
			chatMessage = ChatMessage.builder()
				.id(newId)
				.roomId(chatMessage.getRoomId())
				.senderId(chatMessage.getSenderId())
				.content(chatMessage.getContent())
				.build();
		}

		return mongoTemplate.save(chatMessage);
	}
}