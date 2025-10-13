package com.org.server.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.server.exception.MoiraException;
import com.org.server.redis.service.RedisUserInfoService;
import com.org.server.security.domain.CustomUserDetail;
import com.org.server.util.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static com.org.server.util.jwt.TokenEnum.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
public class JwtAuthFilter extends UsernamePasswordAuthenticationFilter {

    private  RedisUserInfoService redisUserInfoService;
    private  JwtUtil jwtUtils;
    private  AuthenticationManager authenticationManager;

    public JwtAuthFilter(RedisUserInfoService redisUserInfoService, JwtUtil jwtUtils
    ,AuthenticationManager authenticationManager) {
        this.redisUserInfoService = redisUserInfoService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager=authenticationManager;
        setFilterProcessesUrl("/member/login");
    }
    @Override
    public void setFilterProcessesUrl(String filterProcessesUrl) {
        super.setFilterProcessesUrl(filterProcessesUrl);
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try(InputStream inputStream=request.getInputStream()) {

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> data = objectMapper.readValue(inputStream, Map.class);
            String mail=data.get("mail");
            String password=data.get("password");
            UsernamePasswordAuthenticationToken token=
                    new UsernamePasswordAuthenticationToken(mail,password);
            return authenticationManager.authenticate(token);
        }
        catch (IOException e){
            log.debug("유저정보 파싱중 발생한 에러");
            throw new MoiraException("사용자 정보를 불러오기에 실패했습니다",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        CustomUserDetail userDetail=(CustomUserDetail) authResult.getPrincipal();

        String accessToken=jwtUtils.genAccessToken(userDetail.getMemberId());
        String refreshToken=jwtUtils.genRefreshToken(userDetail.getMemberId());
        redisUserInfoService.saveRefreshToken(userDetail.getMemberId(),refreshToken);
        response.setHeader(AUTHORIZATION,TOKEN_PREFIX.getValue()+accessToken);
        response.setStatus(200);
        return;
    }
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {

        if(failed.getClass().getSimpleName().equals("BadCredentialsException")){
            sendErrorResponse(response,HttpStatus.BAD_REQUEST,"비밀번호가 일치하지 않습니다.");
            return ;
        }
        sendErrorResponse(response,HttpStatus.UNAUTHORIZED,failed.getMessage());
    }
    private void sendErrorResponse(HttpServletResponse response, HttpStatus httpStatus, String message) throws
            IOException {
        response.setStatus(httpStatus.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("{\"message\":\"%s\",\"status\":\"%s\"}", message, httpStatus.name()));
    }
}
