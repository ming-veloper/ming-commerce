package com.ming.mingcommerce.security;

import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.exception.MemberException;
import com.ming.mingcommerce.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String password = authentication.getCredentials().toString();
        Member member = memberRepository.findByEmail(authentication.getName()).orElseThrow(() -> new MemberException.MemberEmailNotFoundException("email not found"));

        // SecurityContext 에 담길 인증 객체
        CurrentMember currentMember = modelMapper.map(member, CurrentMember.class);

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new BadCredentialsException("bad credentials!");
        }

        return new UsernamePasswordAuthenticationToken(currentMember, password, authentication.getAuthorities());

    }

    @Override
    public boolean supports(Class<?> authenticationType) {
        return authenticationType.equals(UsernamePasswordAuthenticationToken.class);
    }
}
