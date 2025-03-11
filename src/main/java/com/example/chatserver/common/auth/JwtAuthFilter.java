package com.example.chatserver.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthFilter extends GenericFilter {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String token = httpServletRequest.getHeader("Authorization");

        // TODO 예외 처리 공통 관리적용
        try {
            if (token != null) {
                if (!token.substring(0, 7).equals("Bearer ")) {
                    throw new AuthenticationException("잘못된 Http 요청입니다. Bearer 를 확인하세요");
                }
                // 1. 디코딩하고
                String jwtToken = token.substring(7);

                // 2. JWT 토큰의 헤더, 페이로드와 서버내 secretKey 를 가지고 전달받은 시그니처부를 검증한다
                Claims claims = Jwts.parserBuilder()
                        //------------------------
                        // 검증
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(jwtToken)
                        //------------------------
                        // payload 부분 추출
                        .getBody();
                        //------------------------

                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + claims.get("role"))); //  (관례적) ROLE_ 붙이기; e.g) ROLE_USER
                UserDetails userDetails = new User(claims.getSubject(), "", authorities); // 1) 이메일

                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities()); // 또는 authorities
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            chain.doFilter(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.setContentType("application/json");
            httpServletResponse.getWriter().write("토큰 정보가 유효하지 않습니다");
        }


    }
}
