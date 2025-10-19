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

import com.org.server.chat.domain.ChatRoomMember;
import com.org.server.chat.repository.impl.ChatRoomMemberRepositoryImpl;
import com.org.server.chat.repository.ChatRoomMemberRepository;

@ExtendWith(MockitoExtension.class)
class ChatRoomMemberServiceTest {

	@Mock ChatRoomMemberRepository roomMemberRepository;
	@Mock
	ChatRoomMemberRepositoryImpl roomMemberAdvanceRepository;
	@InjectMocks
	ChatRoomMemberService chatRoomMemberService;

	@Nested @DisplayName("addMembersIfMissing")
	class AddMembersIfMissing {

		@Test @DisplayName("모두 미존재 → 모두 생성")
		void addMembers() {
			Long roomId = 1L;
			List<Long> members = List.of(10L, 20L, 30L);

			for (Long memberId : members) {
				when(roomMemberRepository.findByRoomIdAndTicketId(roomId, memberId))
					.thenReturn(Optional.empty());
			}
			when(roomMemberAdvanceRepository.saveNew(anyLong(), anyLong()))
				.thenReturn(mock(Long.class));

			chatRoomMemberService.addMembersIfMissing(roomId, members);

			for (Long memberId : members) {
				verify(roomMemberRepository).findByRoomIdAndTicketId(roomId, memberId);
				verify(roomMemberAdvanceRepository).saveNew(roomId, memberId);
			}
			verifyNoMoreInteractions(roomMemberRepository);
		}

		@Test @DisplayName("이미 존재하는 회원은 등록하지 않는다")
		void skipExistingMembers() {
			Long roomId = 1L;
			List<Long> members = List.of(10L, 20L);

			when(roomMemberRepository.findByRoomIdAndTicketId(roomId, 10L))
				.thenReturn(Optional.of(mock(ChatRoomMember.class))); // 존재
			when(roomMemberRepository.findByRoomIdAndTicketId(roomId, 20L))
				.thenReturn(Optional.empty()); // 없음
			when(roomMemberAdvanceRepository.saveNew(roomId, 20L))
				.thenReturn(mock(Long.class));

			chatRoomMemberService.addMembersIfMissing(roomId, members);

			verify(roomMemberRepository).findByRoomIdAndTicketId(roomId, 10L);
			verify(roomMemberRepository).findByRoomIdAndTicketId(roomId, 20L);
			verify(roomMemberAdvanceRepository, never()).saveNew(roomId, 10L);
			verify(roomMemberAdvanceRepository).saveNew(roomId, 20L);
			verifyNoMoreInteractions(roomMemberRepository);
		}

	}

	@Nested @DisplayName("removeMember")
	class RemoveMember {

		@Test @DisplayName("memberId가 있으면 삭제 호출")
		void removeExistingMember() {
			Long roomId = 1L;
			Long memberId = 10L;

			chatRoomMemberService.removeMember(roomId, memberId);

			verify(roomMemberRepository).deleteByRoomIdAndTicketId(roomId, memberId);
			verifyNoMoreInteractions(roomMemberRepository);
		}

		@Test @DisplayName("memberId가 null이면 아무 일도 하지 않음")
		void ignoreNullMemberId() {
			Long roomId = 1L;

			chatRoomMemberService.removeMember(roomId, null);

			verifyNoInteractions(roomMemberRepository);
		}
	}

	@Nested @DisplayName("listMemberIds")
	class ListMemberIds {

		@Test @DisplayName("레포 결과를 그대로 반환")
		void returnList() {
			Long roomId = 1L;
			List<Long> expected = List.of(10L, 20L, 30L);

			when(roomMemberAdvanceRepository.findTicketIdsByRoomId(roomId))
				.thenReturn(expected);

			List<Long> result = chatRoomMemberService.listMemberIds(roomId);

			assertThat(result).containsExactlyElementsOf(expected);
			verify(roomMemberAdvanceRepository).findTicketIdsByRoomId(roomId);
			verifyNoMoreInteractions(roomMemberRepository);
		}
	}

	@Nested @DisplayName("isMember")
	class IsMember {

		@Test @DisplayName("roomId 또는 memberId가 null이면 false")
		void nullArgsReturnFalse() {
			assertThat(chatRoomMemberService.isMember(null, 1L)).isFalse();
			assertThat(chatRoomMemberService.isMember(1L, null)).isFalse();
			assertThat(chatRoomMemberService.isMember(null, null)).isFalse();

			// 레포는 호출되지 않아야 함
			verifyNoInteractions(roomMemberRepository);
		}

		@Test @DisplayName("둘 다 유효하면 레포 exists를 위임")
		void delegateToRepository() {
			Long roomId = 1L;
			Long memberId = 10L;

			when(roomMemberRepository.existsByRoomIdAndTicketId(roomId, memberId))
				.thenReturn(true);

			boolean result = chatRoomMemberService.isMember(roomId, memberId);

			assertThat(result).isTrue();
			verify(roomMemberRepository).existsByRoomIdAndTicketId(roomId, memberId);
			verifyNoMoreInteractions(roomMemberRepository);
		}
	}
}