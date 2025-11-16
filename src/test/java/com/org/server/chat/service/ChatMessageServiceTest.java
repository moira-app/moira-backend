package com.org.server.chat.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.org.server.chat.domain.ChatMessage;
import com.org.server.chat.domain.ChatMessageDto;
import com.org.server.chat.domain.ChatType;
import com.org.server.chat.repository.ChatMessageAdvanceRepository;
import com.org.server.chat.repository.ChatMessageRepository;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

	@Mock ChatMessageRepository messageRepository;
	@Mock ChatMessageAdvanceRepository messageAdvanceRepository;

	@InjectMocks ChatMessageService chatMessageService;

	@Nested @DisplayName("sendMessage")
	class SendMessage {

		@Test @DisplayName("정상 전송 시 저장된 엔티티를 DTO로 매핑해 반환한다")
		void send_success() {
			// given
			Long roomId = 100L;
			Long senderId = 10L;
			String content = "hello";
			ChatType type = ChatType.PROJECT;

			ChatMessage saved = ChatMessage.builder()
				.chatType(type)
				.roomId(roomId)
				.senderId(senderId)
				.content(content)
				.build();

			when(messageRepository.save(any(ChatMessage.class))).thenReturn(saved);

			// when
			ChatMessageDto dto = chatMessageService.sendMessage(roomId, senderId, content, type);

			// then
			assertThat(dto.scope()).isEqualTo(type);
			assertThat(dto.roomId()).isEqualTo(roomId);
			assertThat(dto.senderId()).isEqualTo(senderId);
			assertThat(dto.content()).isEqualTo(content);

			verify(messageRepository).save(any(ChatMessage.class));
			verifyNoMoreInteractions(messageRepository, messageAdvanceRepository);
		}

		@Test @DisplayName("roomId/senderId가 null이면 IllegalArgumentException")
		void send_null_ids() {
			assertThatThrownBy(() ->
				chatMessageService.sendMessage(null, 1L, "x", ChatType.PROJECT)
			).isInstanceOf(IllegalArgumentException.class);

			assertThatThrownBy(() ->
				chatMessageService.sendMessage(1L, null, "x", ChatType.PROJECT)
			).isInstanceOf(IllegalArgumentException.class);

			verifyNoInteractions(messageRepository, messageAdvanceRepository);
		}

		@Test @DisplayName("content가 null 또는 공백이면 IllegalArgumentException")
		void send_blank_content() {
			assertThatThrownBy(() ->
				chatMessageService.sendMessage(1L, 1L, null, ChatType.PROJECT)
			).isInstanceOf(IllegalArgumentException.class);

			assertThatThrownBy(() ->
				chatMessageService.sendMessage(1L, 1L, "   ", ChatType.PROJECT)
			).isInstanceOf(IllegalArgumentException.class);

			verifyNoInteractions(messageRepository, messageAdvanceRepository);
		}
	}

	@Nested @DisplayName("listMessagesPage")
	class ListMessagesPage {

		@Test @DisplayName("페이지 결과를 DTO로 매핑해 반환한다")
		void page_mapping() {
			// given
			Long roomId = 100L;
			Pageable pageable = PageRequest.of(0, 10);

			ChatMessage m1 = ChatMessage.builder()
				.chatType(ChatType.PROJECT).roomId(roomId).senderId(1L).content("m1").build();
			ChatMessage m2 = ChatMessage.builder()
				.chatType(ChatType.MEET).roomId(roomId).senderId(2L).content("m2").build();

			Page<ChatMessage> page = new PageImpl<>(List.of(m1, m2), pageable, 2);
			when(messageAdvanceRepository.findPageByRoomId(roomId, pageable)).thenReturn(page);

			// when
			Page<ChatMessageDto> result = chatMessageService.listMessagesPage(roomId, pageable);

			// then
			assertThat(result.getTotalElements()).isEqualTo(2);
			assertThat(result.getContent()).extracting(ChatMessageDto::content)
				.containsExactly("m1", "m2");
			assertThat(result.getContent()).extracting(ChatMessageDto::scope)
				.containsExactly(ChatType.PROJECT, ChatType.MEET);

			verify(messageAdvanceRepository).findPageByRoomId(roomId, pageable);
			verifyNoMoreInteractions(messageAdvanceRepository);
			verifyNoInteractions(messageRepository);
		}
	}

	@Nested @DisplayName("listMessagesSlice")
	class ListMessagesSlice {

		@Test @DisplayName("슬라이스 결과를 DTO로 매핑해 반환한다")
		void slice_mapping() {
			// given
			Long roomId = 200L;
			Pageable pageable = PageRequest.of(0, 5);

			ChatMessage m1 = ChatMessage.builder()
				.chatType(ChatType.PROJECT).roomId(roomId).senderId(3L).content("s1").build();
			ChatMessage m2 = ChatMessage.builder()
				.chatType(ChatType.PROJECT).roomId(roomId).senderId(4L).content("s2").build();

			Slice<ChatMessage> slice = new SliceImpl<>(List.of(m1, m2), pageable, false);
			when(messageAdvanceRepository.findSliceByRoomId(roomId, pageable)).thenReturn(slice);

			// when
			Slice<ChatMessageDto> result = chatMessageService.listMessagesSlice(roomId, pageable);

			// then
			assertThat(result.hasNext()).isFalse();
			assertThat(result.getContent()).extracting(ChatMessageDto::content)
				.containsExactly("s1", "s2");

			verify(messageAdvanceRepository).findSliceByRoomId(roomId, pageable);
			verifyNoMoreInteractions(messageAdvanceRepository);
			verifyNoInteractions(messageRepository);
		}
	}

	@Nested @DisplayName("findLatestMessage")
	class FindLatestMessage {

		@Test @DisplayName("최신 메시지가 있으면 DTO로 반환")
		void latest_exists() {
			// given
			Long roomId = 300L;
			ChatMessage latest = ChatMessage.builder()
				.chatType(ChatType.MEET).roomId(roomId).senderId(9L).content("latest").build();

			when(messageAdvanceRepository.findLatestByRoomId(roomId))
				.thenReturn(Optional.of(latest));

			// when
			ChatMessageDto dto = chatMessageService.findLatestMessage(roomId);

			// then
			assertThat(dto).isNotNull();
			assertThat(dto.content()).isEqualTo("latest");
			assertThat(dto.scope()).isEqualTo(ChatType.MEET);

			verify(messageAdvanceRepository).findLatestByRoomId(roomId);
			verifyNoMoreInteractions(messageAdvanceRepository);
			verifyNoInteractions(messageRepository);
		}

		@Test @DisplayName("최신 메시지가 없으면 null 반환")
		void latest_absent() {
			// given
			Long roomId = 300L;
			when(messageAdvanceRepository.findLatestByRoomId(roomId))
				.thenReturn(Optional.empty());

			// when
			ChatMessageDto dto = chatMessageService.findLatestMessage(roomId);

			// then
			assertThat(dto).isNull();

			verify(messageAdvanceRepository).findLatestByRoomId(roomId);
			verifyNoMoreInteractions(messageAdvanceRepository);
			verifyNoInteractions(messageRepository);
		}
	}
}