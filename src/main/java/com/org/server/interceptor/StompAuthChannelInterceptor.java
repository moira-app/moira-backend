package com.org.server.interceptor;

import java.util.List;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class StompAuthChannelInterceptor  implements ChannelInterceptor {

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor acc = StompHeaderAccessor.wrap(message);

		if (StompCommand.CONNECT.equals(acc.getCommand())) {
			// 예: 토큰 헤더 꺼내기 (필요시 검증 추가)
			String token = acc.getFirstNativeHeader("Authorization");
			// TODO: token 검증하고 userId 추출
			acc.setUser(new UsernamePasswordAuthenticationToken("user-7", null, List.of()));
		}
		return message;
	}

}
