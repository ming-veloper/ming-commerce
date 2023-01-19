package com.ming.mingcommerce;

import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.entity.Role;
import com.ming.mingcommerce.member.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class MingCommerceApplication {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private MemberRepository memberRepository;

    @Value("${admin.email}")
    private String email;

    @Value("${admin.password}")
    private String password;

    public static void main(String[] args) {
        SpringApplication.run(MingCommerceApplication.class, args);
    }

    @PostConstruct
    void adminSave() {
        Member admin = Member.builder()
                .memberName("admin")
                .email(email)
                .role(Role.ADMIN)
                .password(passwordEncoder.encode(password))
                .build();
        memberRepository.save(admin);
    }

}
