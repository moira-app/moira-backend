package com.org.server.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.server.chat.domain.ChatRoomMember;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

	void deleteByRoomIdAndTicketId(Long roomId, Long memberId);

	boolean existsByRoomIdAndTicketId(Long roomId, Long memberId);

	Optional<Object> findByRoomIdAndTicketId(Long roomId, Long memberId);
}
