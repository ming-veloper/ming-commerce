package com.ming.mingcommerce.member.entity;

import com.ming.mingcommerce.member.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberTest {

    @Test
    @DisplayName("이메일 인증을 위한 토큰은 1시간에 1번만 생성 가능하다")
    void issueEmailCheckToken() {
        Member member = Member.builder()
                .email("ming@ming.com")
                .password("ming123@")
                .role(Role.USER)
                .memberName("밍밍이").build();

        member.generateEmailAuthenticationToken();

        assertThrows(MemberException.ExceedEmailTokenIssue.class,
                member::generateEmailAuthenticationToken);
    }

}