package com.ming.mingcommerce.security;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CurrentUser {
    private String uuid;
    private String email;
    private String role;
}
