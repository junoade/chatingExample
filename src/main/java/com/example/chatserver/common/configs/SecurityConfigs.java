package com.example.chatserver.common.configs;

import com.example.chatserver.common.auth.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfigs {

    private final JwtAuthFilter jwtAuthFilter; // 의존성 주입

    public SecurityConfigs(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain myFilter(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable) // csrf 비활성화 // 서비스 로직단에서 방어한다
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic
                // 특정 url 패턴에 대해서는 Authentication 객체를 요구하지 않도록 예외 등록 (인증처리 제외)
                .authorizeHttpRequests(a -> a.requestMatchers("/member/create", "/member/doLogin", "/connect/**").permitAll().anyRequest().authenticated())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션방식 대신 토큰 방식 사용

                // 나머지 url 패턴에선 토큰 검증
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 허용할 origin 도메인
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000"
        ));
        config.setAllowedMethods(Arrays.asList("*")); // 모든 HTTP 메소드 허용
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true); // 자격증명 허용

        // cors를 url 패턴에 지정
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
