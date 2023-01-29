package com.ming.mingcommerce.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.model.JwtTokenModel;
import com.ming.mingcommerce.util.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ModelMapper modelMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CurrentMember currentMember = (CurrentMember) authentication.getPrincipal();
        Member member = modelMapper.map(currentMember, Member.class);

        JwtTokenModel tokenModel = jwtTokenUtil.issueToken(member);
        String result = objectMapper.writeValueAsString(tokenModel);
        response.getWriter().write(result);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }
}
