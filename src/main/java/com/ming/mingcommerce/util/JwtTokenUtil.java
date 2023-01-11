package com.ming.mingcommerce.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ming.mingcommerce.member.model.JwtTokenModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {
    @Value("${jwt.access-token-duration}")
    private long accessTokenDuration;

    @Value("${jwt.refresh-token-duration}")
    private long refreshTokenDuration;

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    /**
     * 사용자 고유 식별이 이메일이 담긴 액세스 토큰과 리프레시 토큰을 생성한다.
     *
     * @param email 이메일
     * @return access token, refresh token
     */

    public JwtTokenModel issueToken(String email) {

        long now = System.currentTimeMillis();

        Algorithm algorithm = Algorithm.HMAC512(jwtSecretKey);
        String accessToken = JWT.create()
                .withIssuedAt(new Date(now))
                .withExpiresAt(new Date(now + accessTokenDuration))
                .withClaim("email", email)
                .sign(algorithm);

        String refreshToken = JWT.create()
                .withIssuedAt(new Date(now))
                .withExpiresAt(new Date(now + refreshTokenDuration))
                .withClaim("email", email)
                .sign(algorithm);

        return JwtTokenModel.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


}
