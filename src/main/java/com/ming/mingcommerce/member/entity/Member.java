package com.ming.mingcommerce.member.entity;

import com.ming.mingcommerce.config.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;
    private String email;
    private String password;
    private String memberName;
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    private String emailCheckToken;

    public void setAdminRole() {
        this.role = Role.ADMIN;
    }

    public void generateEmailAuthenticationToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }

    public Member changeEmail(String email) {
        this.email = email;
        return this;
    }

}
