package com.ming.mingcommerce.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ming.mingcommerce.member.model.JwtTokenModel;
import com.ming.mingcommerce.security.CurrentUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
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

    public JwtTokenModel issueToken(String email, String role) {

        long now = System.currentTimeMillis();

        Algorithm algorithm = Algorithm.HMAC512(jwtSecretKey);
        String accessToken = JWT.create()
                .withIssuedAt(new Date(now))
                .withExpiresAt(new Date(now + accessTokenDuration))
                .withClaim("email", email)
                .withClaim("role", role)
                .sign(algorithm);

        String refreshToken = JWT.create()
                .withIssuedAt(new Date(now))
                .withExpiresAt(new Date(now + refreshTokenDuration))
                .withClaim("email", email)
                .withClaim("role", role)
                .sign(algorithm);

        return JwtTokenModel.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * JWT 토큰을 검증하고 jwt 토큰에 claim 값으로 있는 이메일과 권한을 반환한다.
     *
     * @param token jwt 토큰
     * @return 유저의 이메일
     */
    public CurrentUser verifyToken(String token) {

        Algorithm algorithm = Algorithm.HMAC512(jwtSecretKey);
        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        DecodedJWT decodedJWT = verifier.verify(token);
        String email = decodedJWT.getClaim("email").asString();
        String role = decodedJWT.getClaim("role").asString();

        return new CurrentUser(email, role);
    }


}
