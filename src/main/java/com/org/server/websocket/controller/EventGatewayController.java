package com.org.server.websocket.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.org.server.websocket.domain.EventEnvelope;
import com.org.server.websocket.service.EventHandler;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EventGatewayController {

	private final List<EventHandler> handlers;


	@MessageMapping("/event")
	public void onEvent(EventEnvelope env, Principal principal) {
		handlers.stream()
			.filter(h -> h.supports(env.type()))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Unsupported type: " + env.type()))
			.handle(env, principal);
	}

}
