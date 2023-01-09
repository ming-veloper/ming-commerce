package com.ming.mingcommerce.member.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RegisterResponse {
    private final String accessToken;
    private final String refreshToken;
}
