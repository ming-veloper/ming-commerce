package com.ming.mingcommerce.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CurrentMember {
    private String uuid;
    private String email;
    private String role;
}
