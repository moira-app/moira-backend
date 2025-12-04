package com.org.server.security.filters;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.server.member.domain.Member;
import com.org.server.member.repository.MemberRepository;
import com.org.server.redis.service.RedisUserInfoService;
import com.org.server.security.domain.CustomUserDetail;
import com.org.server.util.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.yaml.snakeyaml.tokens.ScalarToken;
import software.amazon.awssdk.services.s3.model.OptionalObjectAttributes;
import software.amazon.awssdk.thirdparty.jackson.core.JsonParseException;

import java.io.IOException;
import java.util.Arrays;
import static com.org.server.util.jwt.TokenEnum.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import java.util.Optional;
@Slf4j

public class TokenAuthfilter extends OncePerRequestFilter {

    private JwtUtil jwtUtil;
    private RedisUserInfoService redisUserInfoService;
    private MemberRepository memberRepository;

    private ObjectMapper objectMapper;

    public TokenAuthfilter(JwtUtil jwtUtil, RedisUserInfoService redisUserInfoService,
                           MemberRepository memberRepository,ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.redisUserInfoService = redisUserInfoService;
        this.memberRepository = memberRepository;
        this.objectMapper=objectMapper;
    }

    private static final String[] freePassPath = {"/cert","/swagger-ui","/v3/api-docs" ,"/api/chat/"
			,"/ws" ,"/api/","/member/signIn","/member/login"
    };


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return Arrays.stream(freePassPath).anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization=request.getHeader(AUTHORIZATION);
        String accessToken=jwtUtil.getTokenFromHeader(authorization);
        Claims claims;
        if(accessToken==null){
            sendErrorResponse(response,HttpStatus.BAD_REQUEST,"토큰이 없습니다");
            return ;
        }
        if(!jwtUtil.validToken(accessToken)){
            sendErrorResponse(response,HttpStatus.BAD_REQUEST,"유효하지 않은 토큰입니다");
            return ;
        }
        claims=jwtUtil.getClaims(accessToken);
        if(jwtUtil.isExpire(accessToken)){
            String refreshToken=redisUserInfoService.getRefreshToken(claims.get("id",Long.class));
            if(refreshToken==null||jwtUtil.isExpire(refreshToken)){
                response.sendRedirect("redirect");
                return ;
            }
            accessToken=jwtUtil.genAccessToken(claims.get("id", Long.class));
            response.setHeader(AUTHORIZATION,TOKEN_PREFIX.getValue()+accessToken);
        }

        Optional<Member> m=getMemberInfo(claims.get("id",Long.class));

        if(m.isEmpty()){
            sendErrorResponse(response,HttpStatus.BAD_REQUEST,"없는 회원 입니다");
            return;
        }
        CustomUserDetail customUserDetail=new CustomUserDetail(m.get());


        Authentication auth=new UsernamePasswordAuthenticationToken(
                customUserDetail,null,customUserDetail.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request,response);
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus httpStatus, String message) throws
            IOException {
        response.setStatus(httpStatus.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("{\"message\":\"%s\",\"status\":\"%s\"}", message, httpStatus.name()));
    }


    private Optional<Member> getMemberInfo(Long id){
        String userData=redisUserInfoService.getUserInfo(id);
        try {
            if (userData != null) {
                return Optional.of(objectMapper.readValue(userData, Member.class));
            }
            else{
                Optional<Member> m = memberRepository.findById(id);
                if (m.isPresent() && !m.get().getDeleted()) {
                    redisUserInfoService.setUserInfo(m.get().getId(),
                            objectMapper.writeValueAsString(m.get()));
                    return m;
                } else {
                    return Optional.empty();
                }
            }
        }
        catch (Exception e){
            return Optional.empty();
        }
    }
}
