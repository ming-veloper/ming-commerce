package com.ming.mingcommerce.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.entity.Role;
import com.ming.mingcommerce.member.model.JwtTokenModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtTokenUtilTest {
    @Autowired
    JwtTokenUtil jwtTokenService;
    @Value("${jwt.access-token-duration}")
    private Long accessTokenDuration;

    @Test
    @DisplayName("yml 파일 변수 읽기 테스트")
    void ymlReadTest() {
        assertThat(accessTokenDuration).isNotNull();
        assertThat(accessTokenDuration).isGreaterThan(1000);
    }

    @Test
    @DisplayName("jwt 라이브러리 토큰 생성 테스트")
    void token() {
        Algorithm algorithm = Algorithm.HMAC512("this-is-jwt-token-secret!@#");
        String token = JWT.create()
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .sign(algorithm);

        assertThat(token).isInstanceOf(String.class);
    }

    @Test
    @DisplayName("액세스 토큰과 리프레시 토큰을 생성한다")
    void issueToken() {
        Member member = Member.builder()
                .email("tester@ming.com")
                .uuid(UUID.randomUUID().toString())
                .role(Role.USER).build();

        JwtTokenModel tokenModel = jwtTokenService.issueToken(member);
        assertThat(tokenModel).hasFieldOrProperty("refreshToken");
        assertThat(tokenModel).hasFieldOrProperty("accessToken");

        assertThat(tokenModel.getAccessToken()).isNotNull();
        assertThat(tokenModel.getRefreshToken()).isNotNull();

        System.out.println(tokenModel);
    }


}
