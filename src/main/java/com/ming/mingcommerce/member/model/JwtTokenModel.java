package com.ming.mingcommerce.member.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class JwtTokenModel {
    private String accessToken;
    private String refreshToken;
}
