package com.org.server.websocket.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.org.server.websocket.domain.EventEnvelope;
import com.org.server.websocket.service.EventHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;

@Controller
@RequiredArgsConstructor
public class EventGatewayController {
	private final List<EventHandler> handlers;
	private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(EventGatewayController.class);
	/**
	 *
	 * @param env
	 * @param principal
	 *
	 * principal( JWT 연동 기능은 후에 추가할 예정)
	 *
	 */
	@MessageMapping("/event")
	public void onEvent(@Payload EventEnvelope env, Principal principal) {
		log.info("send Message start");

		handlers.stream()
			.filter(h -> h.supports(env.type()))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Unsupported type: " + env.type()))
			.handle(env, principal);
	}

	@MessageMapping("/echo")
	@SendTo("/topic/echo")
	public Map<String, Object> echo(@Payload Map<String, Object> payload) {
		System.out.println("Echoing payload: " + payload);
		return payload;
	}


}
