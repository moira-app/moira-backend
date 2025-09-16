package com.org.server.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.server.chat.domain.ChatRoom;
import com.org.server.chat.domain.ChatType;

public interface ChatRoomRepository  extends JpaRepository<ChatRoom, Long> {

	Optional<ChatRoom> findByChatTypeAndRefId(ChatType chatType, Long refId);

}
