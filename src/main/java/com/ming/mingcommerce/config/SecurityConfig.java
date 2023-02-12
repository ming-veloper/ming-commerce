package com.ming.mingcommerce.config;

import com.ming.mingcommerce.filter.LoginFilter;
import com.ming.mingcommerce.member.repository.MemberRepository;
import com.ming.mingcommerce.security.CustomAuthenticationProvider;
import com.ming.mingcommerce.security.CustomAuthorizationFilter;
import com.ming.mingcommerce.security.LoginSuccessHandler;
import com.ming.mingcommerce.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    private final MemberRepository memberRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final LoginSuccessHandler loginSuccessHandler;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors();
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Permit all
                        .requestMatchers("/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/members/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/members/email-duplication-check").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        // Only for ADMIN
                        .requestMatchers("/api/product-crawl").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .csrf().disable()
                .formLogin().disable();
        // 커스텀 인증 필터 적용
        CustomAuthenticationProvider customAuthenticationProvider = new CustomAuthenticationProvider(memberRepository, passwordEncoder());

        // 커스텀 로그인 필터 적용
        LoginFilter loginFilter = new LoginFilter(customAuthenticationProvider);
        loginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        // 커스텀 인가 필터 적용
        CustomAuthorizationFilter authorizationFilter = new CustomAuthorizationFilter(jwtTokenUtil);
        http.addFilterAt(authorizationFilter, AuthorizationFilter.class);
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
