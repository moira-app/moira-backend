package com.org.server.chat.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.org.server.chat.domain.ChatRoom;
import com.org.server.chat.domain.ChatType;
import com.org.server.chat.repository.ChatRoomRepository;
import com.org.server.chat.service.SequenceService;

import lombok.RequiredArgsConstructor;

// @Profile("mongo")
@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryImpl  implements ChatRoomRepository {

	private final MongoTemplate mongo;
	private final SequenceService seq;

	@Override
	public Optional<ChatRoom> findByChatTypeAndRefId(ChatType chatType, Long refId) {
		Query query = new Query(
			Criteria
				.where("chatType").is(chatType)
				.and("refId").is(refId)
		);

		return Optional.ofNullable(mongo.findOne(query, ChatRoom.class));
	}

	@Override
	public List<ChatRoom> findByRefIdIs(Long refId) {
		Query query = new Query(
			Criteria
				.where("refId").is(refId)
		);


		return  mongo.find(query, ChatRoom.class);
	}

	@Override
	public Optional<ChatRoom> findByid(Long id) {
		Query query = new Query(
			Criteria
				.where("id").is(id)
		);



		return Optional.ofNullable(mongo.findOne(query, ChatRoom.class));
	}

	@Override
	public ChatRoom save(ChatRoom chatRoom) {
		if (chatRoom.getId() == null) {
			// 최초 저장 시에만 시퀀스 발급
			long newId = seq.next("chat_room");
			// 빌더/세터 방식 중 프로젝트 스타일에 맞게 설정
			chatRoom = ChatRoom.builder()
				.id(newId)
				.chatType(chatRoom.getChatType())
				.refId(chatRoom.getRefId())
				.build();
		}
		return mongo.save(chatRoom);
	}
}
