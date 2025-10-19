package com.org.server.chat.repository;

import java.util.List;
import java.util.Optional;

import com.org.server.chat.domain.ChatRoom;
import com.org.server.chat.domain.ChatType;

public interface ChatRoomRepository {

	Optional<ChatRoom> findByChatTypeAndRefId(ChatType chatType, Long refId);

	List<ChatRoom> findByRefIdIs(Long refId);

	Optional<ChatRoom> findByid(Long id);

	ChatRoom save(ChatRoom chatRoom);
}
