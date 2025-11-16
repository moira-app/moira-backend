package com.org.server.chat.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.org.server.chat.domain.ChatRoom;
import com.org.server.chat.domain.ChatType;
import com.org.server.chat.repository.ChatRoomRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {


	private final ChatRoomRepository roomRepository;


	/** 방 보장: (chatType, refId) 기준 멱등 생성 */
	@Transactional
	public ChatRoom ensureRoom(ChatType chatType, Long refId) {
		return roomRepository.findByChatTypeAndRefId(chatType, refId)
			.orElseGet(() -> saveOrFindRoom(chatType, refId));
	}

	private ChatRoom saveOrFindRoom(ChatType chatType, Long refId) {
		try {
			return roomRepository.save(ChatRoom.of(chatType, refId));
		} catch (DataIntegrityViolationException e) {
			// 동시성으로 UNIQUE(chatType, refId) 충돌 시 재조회
			return roomRepository.findByChatTypeAndRefId(chatType, refId)
				.orElseThrow(() -> e);
		} catch (Exception e) {
			// 기타 예외도 재조회로 한 번 더 시도
			return roomRepository.findByChatTypeAndRefId(chatType, refId)
				.orElseThrow(() -> new RuntimeException("Failed to create or find chat room", e));
		}
	}


	/** 방 단건 조회(없으면 예외) */
	@Transactional
	public ChatRoom getRoom(Long roomId) {
		return roomRepository.findByid(roomId)
			.orElseThrow(() -> new IllegalArgumentException("No chat room found with id: " + roomId));
	}

	/** 방 단건 조회(없으면 예외) */
	@Transactional
	public List<ChatRoom> getRooms(Long roomId) {
		return roomRepository.findByRefIdIs((roomId));
	}

}
