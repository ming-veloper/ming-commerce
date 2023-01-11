package com.ming.mingcommerce.security;

import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user email not found."));
        return new User(member.getEmail(), member.getPassword(), extractAuthorities(member));
    }

    private Collection<? extends GrantedAuthority> extractAuthorities(Member member) {
        return Set.of((GrantedAuthority) () -> member.getRole().toString());
    }
}
