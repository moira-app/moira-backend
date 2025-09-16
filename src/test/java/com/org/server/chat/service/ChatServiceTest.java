package com.org.server.chat.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.org.server.chat.domain.ChatRoom;
import com.org.server.chat.domain.ChatType;
import com.org.server.chat.repository.ChatMessageAdvanceRepository;
import com.org.server.chat.repository.ChatMessageRepository;
import com.org.server.chat.repository.ChatRoomRepository;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

	@Mock ChatRoomRepository roomRepository;
	@Mock ChatMessageRepository chatMessageRepository;
	@Mock ChatMessageAdvanceRepository chatMessageAdvanceRepository;

	@InjectMocks ChatService chatService;

	@Nested
	class EnsureRoomTest {

		@Test
		@DisplayName("방이 없으면 생성하여 반환한다")
		void create_when_absent() {
			ChatType chatType = ChatType.PROJECT;
			long refId = 100L;

			when(roomRepository.findByChatTypeAndRefId(chatType, refId))
				.thenReturn(Optional.empty());

			ChatRoom saved = ChatRoom.of(chatType, refId);
			when(roomRepository.save(any(ChatRoom.class)))
				.thenReturn(saved);

			ChatRoom result = chatService.ensureRoom(chatType, refId);

			assertThat(result).isSameAs(saved);
			verify(roomRepository).save(any(ChatRoom.class));
		}

		@Test
		@DisplayName("방이 이미 있으면 생성하지 않고 반환한다")
		void return_when_present() {
			ChatType chatType = ChatType.PROJECT;
			long refId = 100L;

			ChatRoom existing = ChatRoom.of(chatType, refId);
			when(roomRepository.findByChatTypeAndRefId(chatType, refId)).thenReturn(Optional.of(existing));

			ChatRoom result = chatService.ensureRoom(chatType, refId);

			assertThat(result).isSameAs(existing);
			verify(roomRepository, never()).save(any(ChatRoom.class));
		}

	}
}