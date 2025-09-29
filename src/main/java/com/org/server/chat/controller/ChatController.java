package com.org.server.chat.controller;

import com.org.server.chat.application.ChatUseCase;
import com.org.server.chat.domain.ChatMessageDto;
import com.org.server.chat.domain.ChatRoomDto;
import com.org.server.chat.domain.ChatType;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

@Tag(name = "Chat", description = "채팅방 관련 API")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Validated
public class ChatController {

	private final ChatUseCase chatUseCase;
	private final SpringTemplateEngine templateEngine;

	/** 테스트용 Thymeleaf 페이지 (문서 숨김) */
	@Hidden
	@GetMapping(value = "/test", produces = MediaType.TEXT_HTML_VALUE)
	public String test() {
		Context context = new Context();
		return templateEngine.process("chat/chat-test", context);
	}

	/**
	 * UC-1: 방 보장 + 멤버 멱등 추가 + 방 정보 반환
	 * 예) POST /api/chat/rooms?chatType=PROJECT&refId=1001
	 * Body: [10,20,30]
	 */
	@Operation(
		summary = "방 보장 및 멤버 추가",
		description = "chatType/refId로 방을 보장하고 memberIds를 멱등 추가한 뒤 방 정보를 반환합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "성공",
			content = @Content(schema = @Schema(implementation = ChatRoomDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터",
			content = @Content),
		@ApiResponse(responseCode = "401", description = "인증 필요", content = @Content),
		@ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
	})
	@PostMapping(value = "/rooms", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ChatRoomDto> ensureRoomAndAddMembers(
		@Parameter(description = "채팅 타입", required = true)
		@RequestParam @NotNull ChatType chatType,
		@Parameter(description = "레퍼런스 ID", required = true)
		@RequestParam @NotNull Long refId,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "추가할 멤버 ID 목록 (중복 가능, 서버에서 멱등 처리)",
			required = true,
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = Long.class)))
		)
		@RequestBody List<Long> memberIds
	) {
		ChatRoomDto room = chatUseCase.createRoomAndAddMembers(chatType, refId, memberIds);
		return ResponseEntity.ok(room);
	}

	/**
	 * (POST로 조회) refId 기준 방 목록 조회
	 * 예) POST /api/chat/rooms/info?refId=1001
	 */
	@Operation(summary = "방 조회(POST)", description = "refId로 방 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "성공",
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChatRoomDto.class)))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터", content = @Content),
		@ApiResponse(responseCode = "401", description = "인증 필요", content = @Content),
		@ApiResponse(responseCode = "404", description = "방 없음", content = @Content)
	})
	@PostMapping(value = "/rooms/info", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ChatRoomDto>> getRoom(
		@Parameter(description = "레퍼런스 ID", required = true)
		@RequestParam @NotNull Long refId
	) {
		List<ChatRoomDto> rooms = chatUseCase.getRoomInfoList(refId);
		return ResponseEntity.ok(rooms);
	}

	/**
	 * (POST로 조회) 특정 방의 멤버 ID 리스트 조회
	 * 예) POST /api/chat/rooms/{roomId}/members
	 */
	@Operation(summary = "방의 멤버 ID 조회(POST)", description = "roomId로 해당 방의 멤버 ID 목록을 반환합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "성공",
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = Long.class)))),
		@ApiResponse(responseCode = "401", description = "인증 필요", content = @Content),
		@ApiResponse(responseCode = "404", description = "방 없음", content = @Content)
	})
	@PostMapping(value = "/rooms/{roomId}/members", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Long>> listMemberIds(
		@Parameter(description = "방 ID", required = true)
		@PathVariable Long roomId
	) {
		List<Long> ids = chatUseCase.listRoomMemberIds(roomId);
		return ResponseEntity.ok(ids);
	}

	/**
	 * UC-2: roomId 기준 멤버 추가(멱등) → 메시지 전송 → 메시지 반환(페이징)
	 * 예) POST /api/chat/rooms/{roomId}/messages?senderId=7&content=hello
	 * Body: [10,20,30]   // 함께 추가할 멤버 ID들(옵션: 없으면 [])
	 */
	@Operation(
		summary = "멤버 추가 후 메시지 전송(roomId 기준)",
		description = "roomId 기준으로 멤버를 멱등 추가 후, senderId가 content 내용을 전송합니다. 응답은 메시지 페이지입니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "성공",
			content = @Content(schema = @Schema(implementation = PageChatMessageDtoSchema.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터", content = @Content),
		@ApiResponse(responseCode = "401", description = "인증 필요", content = @Content),
		@ApiResponse(responseCode = "404", description = "방 없음", content = @Content)
	})
	@PostMapping(value = "/rooms/{roomId}/messages", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<ChatMessageDto>> addMembersAndSend(
		@Parameter(description = "방 ID", required = true)
		@PathVariable Long roomId,
		@Parameter(description = "보낸 사람 ID", required = true)
		@RequestParam @NotNull Long senderId,
		@Parameter(description = "메시지 내용", required = true)
		@RequestParam @NotBlank String content,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "함께 추가할 멤버 ID 목록 (옵션)",
			required = false,
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = Long.class)))
		)
		@RequestBody(required = false) List<Long> memberIds
	) {
		List<Long> safeMemberIds = (memberIds == null) ? List.of() : memberIds;
		Page<ChatMessageDto> msg = chatUseCase.addMembersAndSend(roomId, safeMemberIds, senderId, content);
		return ResponseEntity.ok(msg);
	}


	@Operation(
		summary = "방의 전체 메시지 조회",
		description = "roomId 기준으로 해당 방의 모든 메시지를 최신 정렬 정책에 따라 반환합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "성공",
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChatMessageDto.class)))),
		@ApiResponse(responseCode = "401", description = "인증 필요", content = @Content),
		@ApiResponse(responseCode = "404", description = "방 또는 메시지 없음", content = @Content)
	})
	@PostMapping(value = "/rooms/{roomId}/messages/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ChatMessageDto>> listMessages(
		@Parameter(description = "방 ID", required = true)
		@PathVariable Long roomId
	) {
		List<ChatMessageDto> msgs = chatUseCase.listMessages(roomId);
		return ResponseEntity.ok(msgs);
	}



	/**
	 * springdoc가 Page<T>를 잘 표현하도록 돕는 래퍼 스키마(문서 전용)
	 * 실제 런타임엔 사용되지 않습니다.
	 */
	@Schema(name = "PageChatMessageDto")
	static class PageChatMessageDtoSchema {
		@Schema(description = "내용 목록")
		public List<ChatMessageDto> content;
		public int number;
		public int size;
		public int numberOfElements;
		public boolean first;
		public boolean last;
		public long totalElements;
		public int totalPages;
		public boolean empty;
	}
}