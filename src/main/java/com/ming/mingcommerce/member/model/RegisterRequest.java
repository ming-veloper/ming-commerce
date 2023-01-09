package com.ming.mingcommerce.member.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterRequest {

    @Email
    private String email;

    @Size(min = 8, max = 16)
    private String password;

    @Size(min = 2, max = 30)
    private String memberName;
}
