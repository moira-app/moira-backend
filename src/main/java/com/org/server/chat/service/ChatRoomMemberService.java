package com.org.server.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.org.server.chat.domain.ChatRoomMemberDto;
import com.org.server.chat.repository.ChatRoomMemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomMemberService {

	private final ChatRoomMemberRepository roomMemberRepository;


	@Transactional
	public void addMembersIfMissing(Long roomId, List<Long> memberIds) {
		for (Long memberId : memberIds) {
			if (memberId == null) continue;
			roomMemberRepository.findByRoomIdAndMemberId(roomId, memberId)
				.orElseGet(() -> roomMemberRepository.saveNew(roomId, memberId));
		}
	}

	/** 멤버 제거(존재하지 않아도 조용히 무시) */
	@Transactional
	public void removeMember(Long roomId, Long memberId) {
		if (memberId != null) {
			roomMemberRepository.deleteByRoomIdAndMemberId(roomId, memberId);
		}
	}

	/** 방의 멤버 ID 리스트 조회 */
	@Transactional
	public List<Long> listMemberIds(Long roomId) {
		return roomMemberRepository.findMemberIdsByRoomId(roomId);
	}

	/** 방 소속 여부 확인 */
	@Transactional
	public boolean isMember(Long roomId, Long memberId) {
		if (roomId == null || memberId == null) return false;
		return roomMemberRepository.existsByRoomIdAndMemberId(roomId, memberId);
	}


}
