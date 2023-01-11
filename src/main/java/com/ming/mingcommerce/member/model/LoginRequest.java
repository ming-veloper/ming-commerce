package com.ming.mingcommerce.member.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest implements Serializable {
    private String email;
    private String password;
}
