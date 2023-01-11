package com.ming.mingcommerce.member.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ming.mingcommerce.member.model.JwtTokenModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtTokenService {
    @Value("${jwt.access-token-duration}")
    private long accessTokenDuration;

    @Value("${jwt.refresh-token-duration}")
    private long refreshTokenDuration;

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    /**
     * 사용자 고유 식별이 가능한 uuid 와 memberName 이 담긴 액세스 토큰과 리프레시 토큰을 생성한다.
     *
     * @param uuid       사용자 고유 식별키
     * @param memberName 사용자의 이름
     * @return access token, refresh token
     */

    public JwtTokenModel issueToken(String uuid, String memberName) {

        long now = System.currentTimeMillis();

        Algorithm algorithm = Algorithm.HMAC512(jwtSecretKey);
        String accessToken = JWT.create()
                .withIssuedAt(new Date(now))
                .withExpiresAt(new Date(now + accessTokenDuration))
                .withClaim("uuid", uuid)
                .withClaim("memberName", memberName)
                .sign(algorithm);

        String refreshToken = JWT.create()
                .withIssuedAt(new Date(now))
                .withExpiresAt(new Date(now + refreshTokenDuration))
                .withClaim("uuid", uuid)
                .withClaim("memberName", memberName)
                .sign(algorithm);

        return JwtTokenModel.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


}
