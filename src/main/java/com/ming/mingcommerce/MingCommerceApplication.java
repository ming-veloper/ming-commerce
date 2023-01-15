package com.ming.mingcommerce;

import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.entity.Role;
import com.ming.mingcommerce.member.repository.MemberRepository;
import com.ming.mingcommerce.product.ProductCrawl;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class MingCommerceApplication {
    @Autowired
    ProductCrawl productCrawl;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private MemberRepository memberRepository;

    public static void main(String[] args) {
        SpringApplication.run(MingCommerceApplication.class, args);
    }

    @PostConstruct
    void adminSave() {
        String password = "1234";
        Member admin = Member.builder()
                .memberName("admin")
                .email("admin@ming.com")
                .role(Role.ADMIN)
                .password(passwordEncoder.encode(password))
                .build();
        memberRepository.save(admin);
    }

}
