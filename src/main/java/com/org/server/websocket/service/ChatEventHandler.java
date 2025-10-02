package com.org.server.websocket.service;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.org.server.chat.application.ChatUseCase;
import com.org.server.chat.domain.ChatMessageDto;
import com.org.server.websocket.domain.EventEnvelope;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatEventHandler implements EventHandler{

	private final ChatUseCase chatUseCase;
	private final SimpMessagingTemplate messagingTemplate;

	@Override
	public boolean supports(String type) {
		return "chat.message".equals(type);
	}

	@Override
	public void handle(EventEnvelope env, java.security.Principal principal) {
		// data: roomId, senderId, content
		Long roomId = ((Number) env.data().get("roomId")).longValue();
		Long senderId = ((Number) env.data().get("senderId")).longValue();
		String content = (String) env.data().get("content");


		List<Long> memberIds = env.data().containsKey("memberIds")
			? ((java.util.List<Number>) env.data().get("memberIds")).stream().map(Number::longValue).toList()
			: java.util.List.of();

		// 도메인 처리: 단건 메시지 전송 (멤버 추가 없음)
		Page<ChatMessageDto> saved = chatUseCase.addMembersAndSend(roomId, memberIds, senderId, content); // 아래 UseCase 추가 코드 참고

		// 브로드캐스트: /topic/chat/room.{roomId}
		messagingTemplate.convertAndSend("/topic/chat/room." + roomId, saved);
	}
}
