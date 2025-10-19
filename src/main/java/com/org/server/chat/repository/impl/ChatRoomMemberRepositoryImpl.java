package com.org.server.chat.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.org.server.chat.domain.ChatRoomMember;
import com.org.server.chat.repository.ChatRoomMemberRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChatRoomMemberRepositoryImpl implements ChatRoomMemberRepository {

	private final MongoTemplate mongo;

	public long saveNew(Long roomId, Long ticketId) {


		Query query = new Query(
			Criteria
				.where("roomId").is(roomId)
				.and("ticketId").is(ticketId)
		);

		Update update = new Update()
			.setOnInsert("roomId", roomId)
			.setOnInsert("ticketId", ticketId);

		try {
			var res = mongo.updateFirst(query, update, ChatRoomMember.class);
			// upsert 된 경우에만 getUpsertedId 가 있음
			return res.getUpsertedId() != null ? 1 : 0;
		} catch (Exception e) {
			e.printStackTrace();

			return 0;
		}
	}

	public List<Long> findTicketIdsByRoomId(Long roomId) {
	 	Query query  = new Query(Criteria.where("roomId").is(roomId));
		query.fields().include("ticketId");

		List<ChatRoomMember> list = mongo.find(query, ChatRoomMember.class);

		return list.stream().map(ChatRoomMember::getTicketId).toList();

	}


	@Override
	public void deleteByRoomIdAndTicketId(Long roomId, Long memberId) {
		Query query = new Query(
			Criteria
				.where("roomId").is(roomId)
				.and("ticketId").is(memberId)
		);

		mongo.remove(query, ChatRoomMember.class);
	}

	@Override
	public boolean existsByRoomIdAndTicketId(Long roomId, Long memberId) {
		Query query = new Query(
			Criteria
				.where("roomId").is(roomId)
				.and("ticketId").is(memberId)
		);

		return mongo.exists(query, ChatRoomMember.class);
	}

	@Override
	public Optional<Object> findByRoomIdAndTicketId(Long roomId, Long memberId) {
		Query query = new Query(
			Criteria
				.where("roomId").is(roomId)
				.and("ticketId").is(memberId)
		);

		ChatRoomMember chatRoomMember = mongo.findOne(query, ChatRoomMember.class);

		return Optional.ofNullable(chatRoomMember);
	}

}
