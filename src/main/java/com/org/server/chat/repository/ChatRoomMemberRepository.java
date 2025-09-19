package com.org.server.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.server.chat.domain.ChatRoomMember;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {


	Object saveNew(Long roomId, Long memberId);

	void deleteByRoomIdAndMemberId(Long roomId, Long memberId);

	List<Long> findMemberIdsByRoomId(Long roomId);

	boolean existsByRoomIdAndMemberId(Long roomId, Long memberId);

	Optional<Object> findByRoomIdAndMemberId(Long roomId, Long memberId);
}
