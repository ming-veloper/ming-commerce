package com.ming.mingcommerce.security;

import com.ming.mingcommerce.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("X-WWW-MING-AUTHORIZATION");

        if (StringUtils.hasText(header)) {
            CurrentMember currentMember = jwtTokenUtil.verifyToken(header.replace("Bearer ", ""));
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(currentMember, "", getAuthorities(currentMember.getRole()));

            SecurityContextHolder.getContext().setAuthentication(token);
        }

        filterChain.doFilter(request, response);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return Set.of((GrantedAuthority) () -> "ROLE_" + role);
    }
}
