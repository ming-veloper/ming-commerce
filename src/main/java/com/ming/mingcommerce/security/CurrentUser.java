package com.ming.mingcommerce.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CurrentUser {
    private String email;
    private String role;
}
