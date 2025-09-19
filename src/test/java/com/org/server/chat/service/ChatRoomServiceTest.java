// src/test/java/com/org/server/chat/service/ChatRoomServiceTest.java
package com.org.server.chat.service;

import com.org.server.chat.domain.ChatRoom;
import com.org.server.chat.domain.ChatType;
import com.org.server.chat.repository.ChatRoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

	@Mock ChatRoomRepository roomRepository;

	@InjectMocks ChatRoomService chatRoomService;

	@Nested @DisplayName("ensureRoom")
	class EnsureRoom {

		@Test @DisplayName("방이 없으면 생성하여 반환한다")
		void create_when_absent() {
			// given
			ChatType chatType = ChatType.PROJECT;
			long refId = 100L;

			when(roomRepository.findByChatTypeAndRefId(chatType, refId))
				.thenReturn(Optional.empty());

			ChatRoom saved = ChatRoom.of(chatType, refId);
			when(roomRepository.save(any(ChatRoom.class))).thenReturn(saved);

			// when
			ChatRoom result = chatRoomService.ensureRoom(chatType, refId);

			// then
			assertThat(result).isSameAs(saved);
			verify(roomRepository).save(any(ChatRoom.class));
		}

		@Test @DisplayName("이미 존재하면 생성하지 않고 기존 방을 반환한다")
		void reuse_when_exists() {
			// given
			ChatType chatType = ChatType.MEET;
			long refId = 7L;

			ChatRoom existing = ChatRoom.of(chatType, refId);
			when(roomRepository.findByChatTypeAndRefId(chatType, refId))
				.thenReturn(Optional.of(existing));

			// when
			ChatRoom result = chatRoomService.ensureRoom(chatType, refId);

			// then
			assertThat(result).isSameAs(existing);
			verify(roomRepository, never()).save(any(ChatRoom.class));
		}

/*
		@Test @DisplayName("동시 생성으로 UNIQUE 충돌 시 재조회하여 기존 방을 반환한다")
		void recover_on_unique_violation() {
			// given
			ChatType chatType = ChatType.PROJECT;
			long refId = 200L;

			// 최초 조회: 없음
			when(roomRepository.findByChatTypeAndRefId(chatType, refId))
				.thenReturn(Optional.empty());

			// 저장 시도: UNIQUE 제약 위반
			when(roomRepository.save(any(ChatRoom.class)))
				.thenThrow(new DataIntegrityViolationException("duplicate"));

			// 충돌 후 재조회: 존재
			when(roomRepository.findByChatTypeAndRefId(chatType, refId))
				.thenReturn(Optional.of(ChatRoom.of(chatType, refId)));

			// when
			ChatRoom result = chatRoomService.ensureRoom(chatType, refId);

			// then
			assertThat(result).isNotNull();
			verify(roomRepository, times(2)).findByChatTypeAndRefId(chatType, refId);
			verify(roomRepository).save(any(ChatRoom.class));
		}
*/
	}

	@Nested @DisplayName("getRoom")
	class GetRoom {

		@Test @DisplayName("존재하는 roomId면 방을 반환한다")
		void ok() {
			// given
			Long roomId = 1L;
			when(roomRepository.findById(roomId))
				.thenReturn(Optional.of(ChatRoom.of(ChatType.PROJECT, 10L)));

			// when
			ChatRoom room = chatRoomService.getRoom(roomId);

			// then
			assertThat(room).isNotNull();
		}

		@Test @DisplayName("존재하지 않는 roomId면 예외를 던진다")
		void not_found() {
			// given
			Long roomId = 9L;
			when(roomRepository.findById(roomId))
				.thenReturn(Optional.empty());

			// expect
			assertThatThrownBy(() -> chatRoomService.getRoom(roomId))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("ChatRoom not found");
		}
	}
}