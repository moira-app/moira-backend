package com.org.server.security.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.server.member.domain.Member;
import com.org.server.member.repository.MemberRepository;
import com.org.server.redis.service.RedisUserInfoService;
import com.org.server.security.domain.CustomOAuth2User;
import com.org.server.util.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

import static com.org.server.util.jwt.TokenEnum.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@RequiredArgsConstructor
@Component
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtils;
    private final RedisUserInfoService redisUserInfoService;
    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customOAuth2User=(CustomOAuth2User) authentication.getPrincipal();

        String accessToken= jwtUtils.genAccessToken(customOAuth2User.getId());
        String refreshToken=jwtUtils.genRefreshToken(customOAuth2User.getId());

        String member=objectMapper.writeValueAsString(customOAuth2User.getMember());
        redisUserInfoService.settingRefreshTokenMemberInfo(customOAuth2User.getId(),member,refreshToken);
        response.addHeader(AUTHORIZATION,TOKEN_PREFIX.getValue()+accessToken);
        //로컬에서 테스트시 원하는 주소로 바꾸시면됩니다.
        response.sendRedirect("http://localhost:3000/login");

        log.info("{} 유저에대한 로그인이 정상적으로 되었습니다",customOAuth2User.getEmail());
    }
}
