package com.org.server.chat.repository;

import java.util.Optional;

public interface ChatRoomMemberRepository {

	void deleteByRoomIdAndTicketId(Long roomId, Long memberId);

	boolean existsByRoomIdAndTicketId(Long roomId, Long memberId);

	Optional<Object> findByRoomIdAndTicketId(Long roomId, Long memberId);
}
