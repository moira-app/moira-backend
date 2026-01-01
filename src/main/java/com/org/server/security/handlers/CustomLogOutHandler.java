package com.org.server.security.handlers;

import com.org.server.redis.service.RedisIntegralService;
import com.org.server.util.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@Slf4j
@Component
public class CustomLogOutHandler implements LogoutSuccessHandler {

    private final RedisIntegralService redisIntegralService;
    private final JwtUtil jwtUtil;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String authorization=request.getHeader(AUTHORIZATION);
        String token=jwtUtil.getTokenFromHeader(authorization);
        if(token==null){
          sendErrorResponse(response,HttpStatus.BAD_REQUEST,"토큰이 없습니다");
          return;
        }
        Claims claims=jwtUtil.getClaims(token);
        Long memberId=claims.get("id",Long.class);
        redisIntegralService.logoutDelMemberInfo(memberId);
        SecurityContextHolder.clearContext();;
        log.info("로그아웃이 성공적으로 처리되었습니다");
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus httpStatus, String message) throws
            IOException {
        response.setStatus(httpStatus.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("{\"message\":\"%s\",\"status\":\"%s\"}", message, httpStatus.name()));
    }

}
