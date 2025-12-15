package com.org.server.websocket.service;

import com.org.server.chat.domain.ChatEvent;
import com.org.server.chat.domain.ChatType;
import com.org.server.chat.service.ChatMessageService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import com.org.server.chat.domain.ChatMessageDto;
import com.org.server.websocket.domain.EventEnvelope;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ChatEventHandler implements EventHandler{
	private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(ChatEventHandler.class);
	private final static String chatRoomPreFix="/topic/chatroom-";
	private final SimpMessagingTemplate messagingTemplate;
	private final ChatMessageService chatMessageService;

	@Override
	public boolean supports(String type) {
		return "chat.message".equals(type);
	}

	@Override
	public void handle(EventEnvelope env, java.security.Principal principal) {
		// data: roomId, senderId, content
		Long roomId = ((Number) env.data().get("roomId")).longValue();
		ChatType chatType= ChatType.valueOf((String)env.data().get("chatType"));
		ChatEvent chatEvent=ChatEvent.valueOf((String) env.data().get("chatEvent"));
		Long senderId = Long.parseLong(principal.getName());
		String content = (String) env.data().get("content");
		String chatId=(String) env.data().getOrDefault("chatId","");
		Long projectId=(Long)env.data().get("projectId");

		switch (chatEvent){
			case CREATE ->{
				ChatMessageDto chatMessageDto=chatMessageService.sendMessage(roomId,senderId,content);
				messagingTemplate.convertAndSend(chatRoomPreFix+projectId+"-"+chatType+"-"+roomId, chatMessageDto);
			}
			case DELETE ->{
				messagingTemplate.convertAndSend(chatRoomPreFix+projectId+"-"+chatType+"-"+ roomId,chatMessageService.delMsg(chatId,senderId));
			}
			case UPDATE ->{
				messagingTemplate.convertAndSend(chatRoomPreFix+projectId+"-"+chatType+"-"+ roomId,chatMessageService.updateMsg(chatId,content,senderId, LocalDateTime.now()));
			}
			default -> {
				throw new RuntimeException();
			}
		}
		// 이후 Kafka 등으로 메시지 큐잉 가능
		//chatUseCase.sendMessage(roomId, senderId , content); // 아래 UseCase 추가 코드 참고
		log.info("send Message end");
	}



}
