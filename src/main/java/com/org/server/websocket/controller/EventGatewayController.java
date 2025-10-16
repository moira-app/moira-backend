package com.org.server.websocket.controller;

import java.security.Principal;
import java.util.List;

import com.org.server.exception.MoiraSocketException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.org.server.websocket.domain.EventEnvelope;
import com.org.server.websocket.service.EventHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
@Tag(name = "WebSocket Event", description = "STOMP 기반 WebSocket 이벤트 처리 컨트롤러 (Swagger 참고용)")
@Log4j2
public class EventGatewayController {

	private final List<EventHandler> handlers;
	private final SimpMessagingTemplate messagingTemplate;

	/**
	 * 클라이언트에서 /app/event 경로로 전송한 메시지를 처리합니다.
	 *
	 * @param env 이벤트 Envelope (type, data, meta)
	 * @param principal 인증 사용자 (JWT 연동 예정)
	 */
	@MessageMapping("/event")
	@Operation(summary = "이벤트 수신 (WebSocket)", description = "STOMP /app/event 로 수신된 메시지를 처리합니다. (Swagger 참고용 문서)")
	public void onEvent(@Payload EventEnvelope env, Principal principal) {
		log.info("send Message start");

		handlers.stream()
			.filter(h -> h.supports(env.type()))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Unsupported type: " + env.type()))
			.handle(env, principal);
	}

	@MessageMapping("/crdt/{projectId}")
	public void onCrdtEvent(@Payload EventEnvelope env, Principal principal,
							@DestinationVariable(value ="projectId") Long projectId){
		log.info("send crdt start");
		handlers.stream()
				.filter(h -> h.supports(env.type()))
				.findFirst()
				.orElseThrow(() -> new MoiraSocketException("Unsupported type: " + env.type()
						,projectId, (String) env.data().get("requestId")))
				.handle(env, principal);
	}


}
