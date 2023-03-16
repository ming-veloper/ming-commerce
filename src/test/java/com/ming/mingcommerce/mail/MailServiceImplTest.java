package com.ming.mingcommerce.mail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;

class MailServiceImplTest {
    @Test
    @DisplayName("Base64 로 이메일과 토큰을 인코딩한다.")
    void base64Encoding() {
        String email = "yeonnex@gmail.com";
        String emailCheckToken = "helloworld";
        String token = email + emailCheckToken;
        String encodedString = Base64.getEncoder().encodeToString(token.getBytes());

        byte[] decode = Base64.getDecoder().decode(encodedString);
        System.out.println("encoded = " + encodedString);
        System.out.println("decoded = " + new String(decode));
    }
}