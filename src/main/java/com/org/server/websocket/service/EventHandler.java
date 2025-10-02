package com.org.server.websocket.service;

import java.security.Principal;

import com.org.server.websocket.domain.EventEnvelope;

public interface EventHandler {
	boolean supports(String type);
	void handle(EventEnvelope env, Principal principal);
}
