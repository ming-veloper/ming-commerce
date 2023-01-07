package com.ming.mingcommerce.member.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegisterRequest {

    @Email
    private String email;

    @Size(min=8, max=16)
    private String password;

    @Size(min=3, max=30)
    private String memberName;
}
