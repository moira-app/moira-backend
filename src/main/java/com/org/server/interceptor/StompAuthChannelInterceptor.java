package com.org.server.interceptor;

import java.util.List;
import java.util.Map;

import com.org.server.exception.MoiraSocketException;
import com.org.server.redis.service.RedisUserInfoService;
import com.org.server.util.jwt.JwtUtil;
import com.org.server.websocket.domain.EventEnvelope;
import io.jsonwebtoken.Claims;
import jdk.jfr.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor  implements ChannelInterceptor {


	private final JwtUtil jwtUtil;
	private final RedisUserInfoService redisUserInfoService;
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor acc = StompHeaderAccessor.wrap(message);

		if (StompCommand.CONNECT.equals(acc.getCommand())) {
			// 예: 토큰 헤더 꺼내기 (필요시 검증 추가)
			String token = acc.getFirstNativeHeader("Authorization");

			// TODO: token 검증하고 userId 추출
			acc.setUser(new UsernamePasswordAuthenticationToken("user-7", null, List.of()));
		}

		//메시지 전송시마다 권한 검증
		if(StompCommand.SEND.equals(acc.getCommand())) {
			String token = acc.getFirstNativeHeader("Authorization");
			if (acc.getDestination().startsWith("/app/crdt")) {
				Claims claims = jwtUtil.getClaims(token);
				Long projectId = Long.parseLong(acc.getDestination().split("/")[2]);
				Long memberId = claims.get("id", Long.class);
				if (!redisUserInfoService.checkTicketKey(String.valueOf(memberId)
						, String.valueOf(projectId))) {

					EventEnvelope env=(EventEnvelope) message.getPayload();
					throw new MoiraSocketException("프로젝트 접근 권한이없습니다",projectId
							,(String) env.data().get("requestId"));
				}
			}
		}
		return message;
	}

}
