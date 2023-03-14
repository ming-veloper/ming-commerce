package com.ming.mingcommerce.member.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailChangeRequest {
    @NotBlank
    private String token;
    @Email
    private String email;
}
