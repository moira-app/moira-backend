package com.org.server.chat.controller;

import com.org.server.chat.application.ChatUseCase;
import com.org.server.chat.domain.ChatMessageDto;
import com.org.server.chat.domain.ChatRoomDto;
import com.org.server.chat.domain.ChatType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Chat", description = "채팅방 관련 API")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Validated
public class ChatController {

	private final ChatUseCase chatUseCase;

	@Operation(
		summary = "방 보장 및 멤버 추가",
		description = "chatType/refId로 방을 보장하고 memberIds를 멱등 추가한 뒤 방 정보를 반환합니다."
	)
	@PostMapping("/rooms")
	public ResponseEntity<ChatRoomDto> ensureRoomAndAddMembers(
		@Parameter(description = "채팅 타입", required = true)
		@RequestParam @NotNull ChatType chatType,
		@Parameter(description = "레퍼런스 ID", required = true)
		@RequestParam @NotNull Long refId,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "추가할 멤버 ID 목록 (중복 가능, 서버에서 멱등 처리)",
			required = true,
			content = @io.swagger.v3.oas.annotations.media.Content(
				array = @ArraySchema(schema = @Schema(implementation = Long.class))
			)
		)
		@RequestBody List<Long> memberIds
	) {
		ChatRoomDto room = chatUseCase.createRoomAndAddMembers(chatType, refId, memberIds);
		return ResponseEntity.ok(room);
	}

	@Operation(summary = "방 조회(POST)", description = "refId로 방 정보를 조회합니다.")
	@PostMapping("/rooms/info")
	public ResponseEntity<ChatRoomDto> getRoom(
		@Parameter(description = "레퍼런스 ID", required = true)
		@RequestParam @NotNull Long refId
	) {
		// ChatUseCase에 getRoomInfo(refId) 가 있다고 가정(없으면 패스스루 메서드 추가)
		ChatRoomDto room = chatUseCase.getRoomInfo(refId);
		return ResponseEntity.ok(room);
	}

	@Operation(summary = "방의 멤버 ID 조회(POST)", description = "roomId로 해당 방의 멤버 ID 목록을 반환합니다.")
	@PostMapping("/rooms/{roomId}/members")
	public ResponseEntity<List<Long>> listMemberIds(
		@Parameter(description = "방 ID", required = true)
		@PathVariable Long roomId
	) {
		// ChatUseCase에 listRoomMemberIds(roomId) 가 있다고 가정(없으면 패스스루 메서드 추가)
		List<Long> ids = chatUseCase.listRoomMemberIds(roomId);
		return ResponseEntity.ok(ids);
	}

	@Operation(summary = "멤버 추가 후 메시지 전송(roomId 기준)",
		description = "roomId 기준으로 멤버를 멱등 추가 후, senderId가 content 내용을 전송합니다.")
	@PostMapping("/rooms/{roomId}/messages")
	public ResponseEntity<ChatMessageDto> addMembersAndSend(
		@Parameter(description = "방 ID", required = true)
		@PathVariable Long roomId,
		@Parameter(description = "보낸 사람 ID", required = true)
		@RequestParam @NotNull Long senderId,
		@Parameter(description = "메시지 내용", required = true)
		@RequestParam @NotBlank String content,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "함께 추가할 멤버 ID 목록 (옵션)",
			required = false,
			content = @io.swagger.v3.oas.annotations.media.Content(
				array = @ArraySchema(schema = @Schema(implementation = Long.class))
			)
		)
		@RequestBody(required = false) List<Long> memberIds
	) {
		List<Long> safeMemberIds = (memberIds == null) ? List.of() : memberIds;
		ChatMessageDto msg = chatUseCase.addMembersAndSend(roomId, safeMemberIds, senderId, content);
		return ResponseEntity.ok(msg);
	}

	@Operation(summary = "참조기준(타입+refId)으로 방 보장 후 메시지 전송",
		description = "chatType/refId 기준으로 방을 보장하고, senderId가 content를 전송합니다.")
	@PostMapping("/messages/by-ref")
	public ResponseEntity<ChatMessageDto> sendByRef(
		@Parameter(description = "채팅 타입", required = true)
		@RequestParam @NotNull ChatType chatType,
		@Parameter(description = "레퍼런스 ID", required = true)
		@RequestParam @NotNull Long refId,
		@Parameter(description = "보낸 사람 ID", required = true)
		@RequestParam @NotNull Long senderId,
		@Parameter(description = "메시지 내용", required = true)
		@RequestParam @NotBlank String content
	) {
		ChatMessageDto msg = chatUseCase.sendByRef(chatType, refId, senderId, content);
		return ResponseEntity.ok(msg);
	}
}