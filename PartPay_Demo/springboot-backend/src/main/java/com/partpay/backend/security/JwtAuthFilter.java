package com.partpay.backend.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        // Always allow CORS preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = request.getHeader("jwt-access-token");
        if (token == null || token.isBlank()) {
            // Allow unauthenticated access only to login and signup
            if (path.equals("/login") || path.equals("/signup")) {
                filterChain.doFilter(request, response);
                return;
            }
            response.setStatus(440);
            response.setContentType("application/json");
            response.getWriter().write("{\"auth\":false,\"message\":\"Session Expired Logging Out.\"}");
            return;
        }
        try {
            Claims claims = jwtService.parseToken(token);
            request.setAttribute("user_id", claims.get("user_id", Integer.class));
            request.setAttribute("org_id", claims.get("org_id", Integer.class));
            request.setAttribute("org_name", claims.get("org_name", String.class));
            request.setAttribute("role", claims.get("role", String.class));
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            response.setStatus(440);
            response.setContentType("application/json");
            response.getWriter().write("{\"auth\":false,\"message\":\"Session Expired Logging Out\"}");
        }
    }
}
