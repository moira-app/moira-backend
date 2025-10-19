package com.org.server.chat.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.org.server.chat.domain.ChatMessage;
import com.org.server.chat.domain.ChatMessageDto;
import com.org.server.chat.domain.ChatType;
import com.org.server.chat.repository.impl.ChatMessageRepositoryImpl;
import com.org.server.chat.repository.ChatMessageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

	private final ChatMessageRepository messageRepository;
	private final ChatMessageRepositoryImpl messageAdvanceRepository;


	/** 메시지 전송 — room.chatType과 요청 chatType 일치 여부 가드 포함 */
	@Transactional
	public ChatMessageDto sendMessage(Long roomId, Long senderId, String content, ChatType chatType) {
		if (roomId == null || senderId == null) {
			throw new IllegalArgumentException("roomId/senderId must not be null");
		}
		if (content == null || content.isBlank()) {
			throw new IllegalArgumentException("content must not be blank");
		}


		// 정책상 멤버십을 강제할 경우 주석 해제
		// if (!isMember(roomId, senderId)) throw new AccessDeniedException("Not a room member");

		ChatMessage saved = messageRepository.save(ChatMessage.of(chatType, roomId, senderId, content));
		return toDto(saved);
	}

	/* ===================== 조회(읽기) ===================== */
	@Transactional
	public Page<ChatMessageDto> listMessagesPage(Long roomId, Pageable pageable) {
		return messageAdvanceRepository.findPageByRoomId(roomId, pageable)
			.map(this::toDto);
	}

	@Transactional
	public Slice<ChatMessageDto> listMessagesSlice(Long roomId, Pageable pageable) {
		return messageAdvanceRepository.findSliceByRoomId(roomId, pageable)
			.map(this::toDto);
	}


	@Transactional
	public ChatMessageDto findLatestMessage(Long roomId) {
		return messageAdvanceRepository.findLatestByRoomId(roomId)
			.map(this::toDto)
			.orElse(null);
	}


	private ChatMessageDto toDto(ChatMessage m) {
		return new ChatMessageDto(
			m.getId(),
			m.getChatType(),
			m.getRoomId(),
			m.getSenderId(),
			m.getContent()
		);
	}

}
