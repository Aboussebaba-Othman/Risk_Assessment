package com.riskassessment.scoring.security;

import com.riskassessment.scoring.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtContextFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String sub = jwtUtil.extractUserId(authHeader);
                if (sub != null) {
                    Long userId = Long.valueOf(sub);
                    UserContextHolder.setUserId(userId);
                    log.debug("Extracted userId {} from JWT and stored in UserContextHolder", userId);
                }
            } catch (Exception e) {
                log.warn("Failed to extract userId from JWT in filter: {}", e.getMessage());
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContextHolder.clear(); 
        }
    }
}
