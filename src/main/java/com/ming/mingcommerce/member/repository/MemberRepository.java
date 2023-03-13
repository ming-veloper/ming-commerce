package com.ming.mingcommerce.member.repository;

import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.exception.MemberException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByEmail(String email);

    Member findMemberByEmail(String email);

    Optional<Member> findByEmailCheckToken(String token);

    default Member findMemberByEmailCheckToken(String token) {
        return findByEmailCheckToken(token)
                .orElseThrow(() -> new MemberException.EmailCheckTokenNotFoundException("해당 emailCheckToken 을 가진 유저가 존재하지 않습니다."));
    }

}
