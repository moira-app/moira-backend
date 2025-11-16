package com.org.server.chat.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.org.server.chat.domain.ChatMessage;

public interface ChatMessageRepository  extends JpaRepository<ChatMessage, Long> {

	Optional<ChatMessage> findTopByRoomIdOrderByIdDesc(Long roomId);
}
