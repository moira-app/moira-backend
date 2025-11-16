package com.org.server.websocket.domain;

import java.util.Map;

public record EventEnvelope(
	String type,          // e.g. "chat.message"
	String version,       // e.g. "1.0"
	Map<String, Object> data,
	Map<String, Object> meta // requestId/ts/actorId 등
) {}