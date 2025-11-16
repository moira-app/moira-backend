package com.org.server.websocket.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.org.server.chat.application.ChatUseCase;
import com.org.server.chat.domain.ChatMessage;
import com.org.server.chat.domain.ChatMessageDto;
import com.org.server.websocket.domain.EventEnvelope;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatEventHandler implements EventHandler{
	private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(ChatEventHandler.class);
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

		ChatMessage saved = ChatMessage.builder()
			.roomId(roomId)
			.senderId(senderId)
			.content(content)
			.build();


		// 브로드캐스트: /topic/chat/room.{roomId}
		messagingTemplate.convertAndSend("/topic/chat/room." + roomId, saved);

		// 이후 Kafka 등으로 메시지 큐잉 가능
		chatUseCase.sendMessage(roomId, senderId , content); // 아래 UseCase 추가 코드 참고

		log.info("send Message end");
	}
}
