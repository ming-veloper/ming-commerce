package com.ming.mingcommerce.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 본 객체를 직렬화 후, Base64 로 인코딩하여 이메일 본문 안에 담길 인증 URL 에 "token" 쿼리 파라미터로 사용된다.
 *
 * <p>https://{domainAddress}/update-user?token={인코딩된 이메일과 토큰이 담긴 객체}<p/>
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailAuthenticationToken implements Serializable {
    private String email;
    private String emailCheckToken;
}
