package com.org.server.websocket.domain;

import lombok.Builder;

import java.util.Map;

@Builder
public record EventEnvelope(
	String type,          // e.g. "chat.message"
	String version,       // e.g. "1.0"
	Map<String, Object> data,
	Map<String, Object> meta // requestId/ts/actorId 등
) {}