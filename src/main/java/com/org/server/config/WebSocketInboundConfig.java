package com.org.server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.org.server.interceptor.StompAuthChannelInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebSocketInboundConfig implements WebSocketMessageBrokerConfigurer {

	private final StompAuthChannelInterceptor auth;

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(auth);
	}
}

