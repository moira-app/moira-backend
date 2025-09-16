package com.org.server.chat.service;

import org.springframework.stereotype.Service;

import com.org.server.chat.domain.ChatMessage;
import com.org.server.chat.domain.ChatMessageDto;
import com.org.server.chat.domain.ChatRoom;
import com.org.server.chat.domain.ChatType;
import com.org.server.chat.repository.ChatMessageAdvanceRepository;
import com.org.server.chat.repository.ChatMessageRepository;
import com.org.server.chat.repository.ChatRoomRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

	private final ChatRoomRepository roomRepository;
	private final ChatMessageRepository messageRepository;
	private final ChatMessageAdvanceRepository messageAdvanceRepository;


	/*
	* [BJW] ChatService
	* 기능 리스트
	* 1) ensureRoom(scope, refId) : 방 보장(있으면 반환/없으면 생성)
	* 2) getRoom(roomId)          : 방 단건 조회
	* 3) sendMessage(roomId, senderId, content) : 메시지 전송
	* 4) listMessagesPage(roomId, pageable)     : 메시지 페이지 조회(Page, total 포함)
	* 5) listMessagesSlice(roomId, pageable)    : 메시지 슬라이스 조회(Slice, hasNext만)
	* 6) findLatestMessage(roomId)              : 최신 메시지 1개
	**/


	@Transactional
	public ChatRoom ensureRoom(ChatType chatType, Long refId) {
		return roomRepository.findByChatTypeAndRefId(chatType, refId)
			.orElseGet(() ->  saveOrFindRoom(chatType, refId) );

	}

	private ChatRoom saveOrFindRoom(ChatType chatType, Long refId) {
		try {
			return roomRepository.save(ChatRoom.of(chatType, refId));
		} catch (Exception e) {
			// 동시성 문제로 이미 생성된 경우 다시 조회
			return roomRepository.findByChatTypeAndRefId(chatType, refId)
				.orElseThrow(() -> new RuntimeException("Failed to create or find chat room"));
		}
	}

	// --- 2) 방 단건 조회 ---
	@Transactional
	public ChatRoom getRoom(Long roomId) {
		return roomRepository.findById(roomId)
			.orElseThrow(() -> new IllegalArgumentException("ChatRoom not found: " + roomId));
	}

	// --- 3) 메시지 전송 ---
	@Transactional
	public ChatMessageDto sendMessage(Long roomId, Long senderId, String content, ChatType chatType) {
		if (roomId == null || senderId == null) {
			throw new IllegalArgumentException("roomId/senderId must not be null");
		}
		if (content == null || content.isBlank()) {
			throw new IllegalArgumentException("content must not be blank");
		}

		// 방 존재 보장 (권한 검증은 외부에서)
		getRoom(roomId);

		ChatMessage saved = messageRepository.save(ChatMessage.of(chatType, roomId, senderId, content));
		return toDto(saved);
	}



	// --- 변환 ---
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
