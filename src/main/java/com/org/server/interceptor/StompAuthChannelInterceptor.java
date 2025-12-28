package com.org.server.interceptor;


import java.util.Optional;

import com.org.server.exception.SocketAuthError;
import com.org.server.member.domain.Member;
import com.org.server.member.repository.MemberRepository;
import com.org.server.redis.service.RedisUserInfoService;
import com.org.server.util.jwt.JwtUtil;
import com.org.server.websocket.domain.StompPrincipal;
import com.org.server.websocket.service.RedisStompService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompAuthChannelInterceptor  implements ChannelInterceptor {


	private final JwtUtil jwtUtil;
	private final RedisUserInfoService redisUserInfoService;
    private final MemberRepository memberRepository;
    private final RedisStompService redisStompService;
    private final static String noTicketError="NoTicket";
    private final static String noMemberError="NoMember";
    private final static String noAccessToken="NoToken";

    private final static String existSession="SessionAlreadyExist";
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor acc = MessageHeaderAccessor.getAccessor(message,StompHeaderAccessor.class);
        if(acc.getCommand().equals(StompCommand.DISCONNECT)){
            return message;
        }
        if(acc.getCommand().equals(StompCommand.SUBSCRIBE)||acc.getCommand().equals(StompCommand.SEND)
                ||acc.getCommand().equals(StompCommand.CONNECT)){
            //이하 subscribe,connect,send의 경우 모두 메시지 검증.
            String token = jwtUtil.getTokenFromHeader(acc.getFirstNativeHeader("Authorization"));
            if (token == null) {
                throw new SocketAuthError(noAccessToken);
            }
            Claims claims = jwtUtil.getClaims(token);
            Long memberId = claims.get("id", Long.class);
            checkMemberExist(memberId);
            if (StompCommand.SEND.equals(acc.getCommand())){
               handleSendMessage(memberId, acc);
            }
            if(StompCommand.CONNECT.equals(acc.getCommand())){
                if(checkStompSessionExist(memberId.toString())){
                    throw new SocketAuthError(existSession);
                }
                StompPrincipal stompPrincipal=new StompPrincipal(memberId.toString());
                acc.setUser(stompPrincipal);
                return MessageBuilder.createMessage(message.getPayload(),acc.getMessageHeaders());
            }
        }
	    return message;
	}
    private void checkMemberExist(Long memberId){
        if(!redisUserInfoService.CheckMemberExist(memberId)){
            Optional<Member> m=memberRepository.findById(memberId);
            if(m.isEmpty()||m.get().getDeleted()){
                throw new SocketAuthError(noMemberError);
            }
        }
    }
    private Long getProjectIdFromDest(String dest){
        if(dest.startsWith("/app/crdt")||dest.startsWith("/app/signaling")){
            Long projectId = Long.parseLong(dest.split("/")[3]);
            return projectId;
        }
        return -1L;
    }
    private void handleSendMessage(Long memberId,StompHeaderAccessor acc){
        //메시지 전송시마다 권한 검증
        Long projectId=getProjectIdFromDest(acc.getDestination());
        if (projectId>-1&&!redisUserInfoService.checkTicketKey(String.valueOf(memberId)
                , String.valueOf(projectId))) {
            throw new SocketAuthError(noTicketError);
        }
    }
    private boolean checkStompSessionExist(String memberId){
        return redisStompService.checkSessionKeyExist(memberId);
    }
}

