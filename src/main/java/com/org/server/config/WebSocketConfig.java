package com.org.server.config;

import java.util.List;

import com.org.server.interceptor.CustomHandShakeHandler;
import com.org.server.interceptor.CustomHandShakeInterceptor;
import com.org.server.interceptor.StompErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


	private final StompErrorHandler stompErrorHandler;
	private final CustomHandShakeHandler customShakeHandler;
	private final CustomHandShakeInterceptor customHandShakeInterceptor;
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
				.setAllowedOriginPatterns("*")
				.addInterceptors(customHandShakeInterceptor)
				.setHandshakeHandler(customShakeHandler)
				.withSockJS()
				.setHeartbeatTime(30000);
		registry.setErrorHandler(stompErrorHandler);
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic", "/queue", "/user");
		registry.setUserDestinationPrefix("/user");
		registry.setApplicationDestinationPrefixes("/app");
	}


	// JSON 직렬화(메시지 컨버터) 확실히
	@Override
	public boolean configureMessageConverters(List<MessageConverter> converters) {
		MappingJackson2MessageConverter jackson = new MappingJackson2MessageConverter();
		jackson.setObjectMapper(new ObjectMapper());
		converters.add(jackson);
		return false; // 기본 컨버터도 유지
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration reg) {
		reg.interceptors(new ChannelInterceptor() {
			@Override public Message<?> preSend(Message<?> m, MessageChannel c) {
				var a = StompHeaderAccessor.wrap(m);
				System.out.println("[INBOUND] " + a.getCommand() + " dest=" + a.getDestination());
				return m;
			}
		});
	}

	@Override
	public void configureClientOutboundChannel(ChannelRegistration reg) {
		reg.interceptors(new ChannelInterceptor() {
			@Override public Message<?> preSend(Message<?> m, MessageChannel c) {
				var a = StompHeaderAccessor.wrap(m);
				System.out.println("[OUTBOUND] " + a.getCommand() + " dest=" + a.getDestination());
				return m;
			}
		});
	}

}
