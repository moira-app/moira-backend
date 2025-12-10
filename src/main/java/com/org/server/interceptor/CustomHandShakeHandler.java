package com.org.server.interceptor;

import com.org.server.util.jwt.JwtUtil;
import com.org.server.websocket.domain.StompPrincipal;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;


//@Component
@RequiredArgsConstructor
@Slf4j
public class CustomHandShakeHandler extends DefaultHandshakeHandler {

    private final JwtUtil jwtUtil;

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        HttpHeaders httpHeaders=request.getHeaders();
        String header=(String) httpHeaders.get("Authorization").getFirst();
        String token=jwtUtil.getTokenFromHeader(header);
        Claims claims=jwtUtil.getClaims(token);
        Long memberId=claims.get("id",Long.class);
        return new StompPrincipal(String.valueOf(memberId));
    }
}
