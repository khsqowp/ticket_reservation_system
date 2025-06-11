package com.example.ticket_reservation_system.config.jwt;

import com.example.ticket_reservation_system.security.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService; // UserDetailsService 주입

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenValue = jwtUtil.getJwtFromHeader(request);

        if (tokenValue != null) {
            if (!jwtUtil.validateToken(tokenValue)) {
                sendErrorResponse(response, "토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
                return;
            }
            try {
                Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error("Token validation error", e);
                sendErrorResponse(response, "토큰 검증 중 오류가 발생했습니다.", HttpStatus.FORBIDDEN);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username); // 실제 DB에서 사용자 정보 조회
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private void sendErrorResponse(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(status.value());
        response.getWriter().write(new ObjectMapper().writeValueAsString(Map.of("error", message)));
    }
}