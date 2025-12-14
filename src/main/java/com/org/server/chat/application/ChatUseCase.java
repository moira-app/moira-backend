package com.org.server.chat.application;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.org.server.chat.domain.ChatMessageDto;
import com.org.server.chat.domain.ChatRoom;
import com.org.server.chat.domain.ChatRoomDto;
import com.org.server.chat.domain.ChatType;
import com.org.server.chat.service.ChatMessageService;
import com.org.server.chat.service.ChatRoomMemberService;
import com.org.server.chat.service.ChatRoomService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatUseCase {

	private final ChatRoomService roomService;
	private final ChatMessageService messageService;
	private final ChatRoomMemberService roomMemberService;


	/** UC-1: 방 보장 → 멤버 추가(멱등) → 방 정보 반환 */
	@Transactional
	public ChatRoomDto createRoomAndAddMembers(ChatType chatType, Long refId, List<Long> memberIds) {
		ChatRoom room = roomService.ensureRoom(chatType, refId);
		roomMemberService.addMembersIfMissing(room.getId(), memberIds);
		return new ChatRoomDto(room.getId(), room.getChatType(), room.getRefId());
	}

	/** UC-2: roomId 기준 멤버 추가(멱등) → 메시지 전송 → 메시지 반환 */
	public List<ChatMessageDto> addMembersAndSend(Long roomId, List<Long> memberIds, Long senderId, String content) {
		ChatRoom room = roomService.getRoom(roomId);
		roomMemberService.addMembersIfMissing(room.getId(), memberIds);
		messageService.sendMessage(room.getId(), senderId, content, room.getChatType());
		return messageService.getMsgList(null,roomId);
	}

	public ChatMessageDto sendMessage(Long roomId, Long senderId, String content) {
		ChatRoom room = roomService.getRoom(roomId);
		return messageService.sendMessage(room.getId(), senderId, content, room.getChatType());
	}



	public List<ChatMessageDto> listMessages(String offset,Long roomId) {
		return messageService.getMsgList(offset,roomId);
	}


	@Transactional
	public List<ChatRoomDto> getRoomInfoList(Long roomId) {
		return roomService.getRooms(roomId)
			.stream()
			.map(chatRoom -> new ChatRoomDto(chatRoom.getId(), chatRoom.getChatType(), chatRoom.getRefId()))
			.toList();
	}


	@Transactional
	public ChatRoomDto getRoomInfo(Long roomId) {
		ChatRoom room = roomService.getRoom(roomId);
		return new ChatRoomDto(room.getId(), room.getChatType(), room.getRefId());
	}


	@Transactional
	public List<Long> listRoomMemberIds(Long roomId) {
		return roomMemberService.listMemberIds(roomId);
	}
}
