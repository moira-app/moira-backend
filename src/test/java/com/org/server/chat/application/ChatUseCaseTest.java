package com.org.server.chat.application;


import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import com.org.server.chat.domain.ChatMessageDto;
import com.org.server.chat.domain.ChatRoomDto;
import com.org.server.chat.domain.ChatType;
import com.org.server.chat.service.ChatMessageService;
import com.org.server.chat.service.ChatRoomMemberService;
import com.org.server.chat.service.ChatRoomService;

import jakarta.transaction.Transactional;

@SpringBootTest
class ChatUseCaseTest {

	@Autowired ChatUseCase chatUseCase;
	@Autowired ChatRoomService roomService;
	@Autowired ChatRoomMemberService roomMemberService;
	@Autowired ChatMessageService messageService;

	@Nested @DisplayName("UC-1: createRoomAndAddMembers")
	class CreateRoomAndAddMembersIT {

		@Test @Transactional
		@DisplayName("방 보장 + 멤버 멱등 추가 + 방 정보 반환")
		void createRoomAndAddMembers_idempotent() {
			// given
			ChatType chatType = ChatType.PROJECT;
			Long refId = 1001L;
			List<Long> members = List.of(10L, 20L, 30L, 20L, 10L); // 중복 포함

			// when: 최초 호출
			ChatRoomDto room1 = chatUseCase.createRoomAndAddMembers(chatType, refId, members);

			// then
			assertThat(room1).isNotNull();
			assertThat(room1.chatType()).isEqualTo(chatType);
			assertThat(room1.refId()).isEqualTo(refId);

			// 멤버가 중복 없이 추가됐는지
			List<Long> memberIds1 = roomMemberService.listMemberIds(room1.id());
			assertThat(memberIds1).containsExactlyInAnyOrder(10L, 20L, 30L);

			// when: 동일 파라미터로 재호출 (멱등)
			ChatRoomDto room2 = chatUseCase.createRoomAndAddMembers(chatType, refId, members);

			// then: 같은 방이어야 함
			assertThat (room2.id()).isEqualTo(room1.id());
			List<Long> memberIds2 = roomMemberService.listMemberIds(room2.id());
			assertThat(memberIds2).containsExactlyInAnyOrder(10L, 20L, 30L);
		}
	}

	@Nested @DisplayName("UC-2: addMembersAndSend")
	class AddMembersAndSendIT {

		@Test @Transactional
		@DisplayName("roomId 기준 멤버 추가(멱등) 후 메시지 전송")
		void addMembersAndSend_flow() {
			// given: 우선 방을 하나 보장
			ChatType chatType = ChatType.MEET;
			Long refId = 2002L;
			Long senderId = 99L;
			String content = "hello, meet!";
			List<Long> members = List.of(1L, 2L, 3L);

			Long roomId = roomService.ensureRoom(chatType, refId).getId();

			// when
			ChatMessageDto msg = chatUseCase.addMembersAndSend(roomId, members, senderId, content).getContent().get(0);

			// then: 메시지 확인
			assertThat(msg).isNotNull();
			assertThat(msg.scope()).isEqualTo(chatType);
			assertThat(msg.roomId()).isEqualTo(roomId);
			assertThat(msg.senderId()).isEqualTo(senderId);
			assertThat(msg.content()).isEqualTo(content);

			// 멤버 추가 확인(멱등)
			List<Long> roomMembers = roomMemberService.listMemberIds(roomId);
			assertThat(roomMembers).containsExactlyInAnyOrderElementsOf(members);

			// 메시지 조회로도 검증(페이지)
			var page = messageService.listMessagesPage(roomId, PageRequest.of(0, 10));
			assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(1);
			assertThat(page.getContent())
				.extracting(ChatMessageDto::content)
				.contains(content);
		}
	}

}