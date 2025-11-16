package com.org.server.interceptor;


import java.util.List;

import com.org.server.exception.MoiraSocketException;
import com.org.server.redis.service.RedisUserInfoService;
import com.org.server.util.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import static com.org.server.util.jwt.TokenEnum.TOKEN_PREFIX;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompAuthChannelInterceptor  implements ChannelInterceptor {


	private final JwtUtil jwtUtil;
	private final RedisUserInfoService redisUserInfoService;
    private final static String noTicketError="NoTicket";
    private final static String noAccessToken="NoToken";
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor acc = StompHeaderAccessor.wrap(message);

        if(acc.getCommand().equals(StompCommand.DISCONNECT)||acc.getCommand().equals(StompCommand.SUBSCRIBE)){
            return message;
        }
        String token=jwtUtil.getTokenFromHeader(acc.getFirstNativeHeader("Authorization"));
        if(token==null){
            throw new MoiraSocketException(noAccessToken,0L,"테스트 에러","");
        }
        Claims claims = jwtUtil.getClaims(token);
		if (StompCommand.CONNECT.equals(acc.getCommand())) {
			// TODO: token 검증하고 userId 추출
			acc.setUser(new UsernamePasswordAuthenticationToken("user-7", null, List.of()));
		    return message;
        }
        if(StompCommand.SEND.equals(acc.getCommand())) {
            handleCrdtSendMessage(claims.get("memberId",Long.class), acc);
        }
	    return message;
	}

    private void handleCrdtSendMessage(Long memberId,StompHeaderAccessor acc){
        //메시지 전송시마다 권한 검증
        if (acc.getDestination().startsWith("/app/crdt")) {
                Long projectId = Long.parseLong(acc.getDestination().split("/")[3]);
                /*if (!redisUserInfoService.checkTicketKey(String.valueOf(memberId)
                        , String.valueOf(projectId))) {
                    throw new MoiraSocketException(noTicketError, projectId,"테스트 에러","");
                }*/
            //throw new MoiraSocketException(noTicketError, projectId,"테스트 에러","");
        }
    }

}
