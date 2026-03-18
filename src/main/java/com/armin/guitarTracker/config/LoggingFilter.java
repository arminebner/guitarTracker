package com.armin.guitarTracker.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String method = request.getMethod();
        String path = request.getRequestURI();
        String contentType = request.getContentType();
        
        System.out.println("[LOGGING FILTER] " + method + " " + path + " | Content-Type: " + contentType);
        
        if (path.contains("/authenticate")) {
            System.out.println("[LOGGING FILTER] Authenticate request detected");
            System.out.println("[LOGGING FILTER] Request headers: Authorization=" + request.getHeader("Authorization"));
        }
        
        filterChain.doFilter(request, response);
    }
}

