package com.ming.mingcommerce.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ming.mingcommerce.member.entity.Role;
import com.ming.mingcommerce.member.model.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Component
public class LoginFilter extends AbstractAuthenticationProcessingFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${admin.email}")
    private String adminEmail;

    public LoginFilter(AuthenticationProvider authenticationProvider) {
        super("/api/login");
        super.setAuthenticationManager(new ProviderManager(authenticationProvider));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {

        String string = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        LoginRequest loginRequest = objectMapper.readValue(string, LoginRequest.class);

        Role role = Role.USER;
        // 로그인 하는 이메일이 admin@ming.com 일 경우에 ADMIN 권한 부여
        if (Objects.equals(loginRequest.getEmail(), adminEmail)) {
            role = Role.ADMIN;
        }
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                loginRequest.getPassword(), getAuthorities(role));
        return getAuthenticationManager().authenticate(token);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Role role) {
        return Set.of((GrantedAuthority) role::name);
    }
}
