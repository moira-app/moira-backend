package com.org.server.config;


import com.org.server.member.repository.MemberRepository;
import com.org.server.member.service.SecurityMemberReadService;
import com.org.server.redis.service.RedisUserInfoService;
import com.org.server.security.detailservices.CustomOAuth2Service;
import com.org.server.security.detailservices.CustomUserDetailService;
import com.org.server.security.filters.JwtAuthFilter;
import com.org.server.security.filters.ProjectTicketFilter;
import com.org.server.security.filters.TokenAuthfilter;
import com.org.server.security.handlers.CustomLogOutHandler;
import com.org.server.security.handlers.OAuth2FailHandler;
import com.org.server.security.handlers.OAuth2SuccessHandler;
import com.org.server.ticket.service.TicketService;
import com.org.server.util.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailHandler oAuth2FailHandler;
    private final CustomLogOutHandler customLogOutHandler;
    private final RedisUserInfoService redisUserInfoService;
    private final MemberRepository memberRepository;
    private final WebConfig webConfig;
    private final JwtUtil jwtUtil;
    private final TicketService ticketService;


	private static final String[] freePassUrl = {
		"/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**", "/graph/**",
		"/cert/**",
		"/api/chat/**",          // Thymeleaf 테스트 페이지 라우트가 /api/chat/test인 경우
		"/api/**",          // Thymeleaf 테스트 페이지 라우트가 /api/chat/test인 경우
		"/chat-test", "/chat-socket-test", // 뷰컨트롤러로 열었을 때
		"/ws/**",                   // SockJS 핸드셰이크(/ws/info 등)
            "/member/signIn","/member/logout"

	};

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{


        http.headers(headers -> headers
                .httpStrictTransportSecurity(HeadersConfigurer.HstsConfig::disable)
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
        );

        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());


        // http.logout(AbstractHttpConfigurer::disable)
        //         .formLogin(AbstractHttpConfigurer::disable)
        //         .httpBasic(AbstractHttpConfigurer::disable)
        //         .cors(AbstractHttpConfigurer::disable);

        http.addFilterBefore(new JwtAuthFilter(redisUserInfoService,jwtUtil,authenticationManager()
        ), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(new TokenAuthfilter(jwtUtil,redisUserInfoService,memberRepository)
                , JwtAuthFilter.class);
        http.addFilterAfter(new ProjectTicketFilter(redisUserInfoService,ticketService),TokenAuthfilter.class);

        http.sessionManagement(session->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.oauth2Login(oauth2->
                oauth2.userInfoEndpoint(userInfo->userInfo.userService(
                        new CustomOAuth2Service(memberRepository)
                        ))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailHandler)
        );

		// CSRF: API/Swagger/WS 경로는 무시 (완전 disable 대신 부분 무시 권장)
		http.csrf(csrf -> csrf
			.ignoringRequestMatchers("/api/**", "/ws/**", "/swagger-ui/**", "/v3/api-docs/**","/member/**",
                    "/enter/**")
		);

        http.cors(c->c.configurationSource(webConfig.corsConfigurationSource()));

        http.logout(logout -> logout
                .logoutUrl("/member/logout")
                .invalidateHttpSession(true)
                .logoutSuccessHandler(customLogOutHandler)
                .permitAll());

        http.authorizeHttpRequests(req->
                req.requestMatchers(freePassUrl).permitAll()
                        .anyRequest().authenticated());

        return http.build();
    };

    @Bean
    public AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider(
                new CustomUserDetailService(memberRepository)
        );
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }


}
