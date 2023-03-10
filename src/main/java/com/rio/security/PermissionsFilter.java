package com.rio.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class PermissionsFilter extends OncePerRequestFilter {

    private final PermissionsService permissionsService;
    private final RequestMatcher requestMatcher;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (requestMatcher.matches(request)) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            String[] parts = request.getServletPath().split("/");
            Long id = Long.valueOf(parts[parts.length - 1]);

            if (!permissionsService.hasPermissions(authentication, id)) {
                response.setStatus(403);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
