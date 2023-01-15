package com.ming.mingcommerce.config;

import com.ming.mingcommerce.filter.LoginFilter;
import com.ming.mingcommerce.member.repository.MemberRepository;
import com.ming.mingcommerce.security.CustomAuthenticationProvider;
import com.ming.mingcommerce.security.CustomAuthorizationFilter;
import com.ming.mingcommerce.security.CustomUserDetailsService;
import com.ming.mingcommerce.security.LoginSuccessHandler;
import com.ming.mingcommerce.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Permit all
                        .requestMatchers(HttpMethod.POST, "/api/members/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/members/email-duplication-check").permitAll()
                        // Only for ADMIN
                        .requestMatchers("/api/product-crawl").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .csrf().disable()
                .formLogin().disable();

        // 커스텀 로그인 필터 적용
        LoginFilter loginFilter = new LoginFilter(authenticationManager(http));
        loginFilter.setAuthenticationSuccessHandler(new LoginSuccessHandler(jwtTokenUtil));
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

    @Bean
    UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(memberRepository);
    }


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .authenticationProvider(new CustomAuthenticationProvider(userDetailsService(), passwordEncoder()));

        return authenticationManagerBuilder.build();
    }
}
