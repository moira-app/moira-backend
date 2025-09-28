package com.org.server.chat.repository;

import java.util.List;
import java.util.Optional;

import javax.swing.text.html.Option;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.server.chat.domain.ChatRoom;
import com.org.server.chat.domain.ChatType;

public interface ChatRoomRepository  extends JpaRepository<ChatRoom, Long> {

	Optional<ChatRoom> findByChatTypeAndRefId(ChatType chatType, Long refId);

	Optional<ChatRoom> findByRefId(Long refId);

	List<ChatRoom> findByRefIdIs(Long refId);

	Optional<ChatRoom> findByid(Long id);
}
