package com.org.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Configuration
public class WebConfig{


    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config=new CorsConfiguration();

        /*
         * 브라우저가 쿠키 혹은 AUTHROZATION같은 헤더를 포함할수있게 해주는 설정.
         * */
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000","http://localhost:5173"));
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");

        config.addExposedHeader(AUTHORIZATION);
        /* 백엔드의 response에 담는 헤더르 프론트단에서 볼수있게 설정해주는것.*/

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        //경로마다 어떤 CORS정책을 설정할지를 말하는대 /**이므로 모든 겨올에대해서 COSR를 같은걸 적용한다 이말.

        return source;
    }

}
