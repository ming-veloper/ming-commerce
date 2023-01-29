package com.ming.mingcommerce.member.entity;

import com.ming.mingcommerce.config.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public void setAdminRole() {
        this.role = Role.ADMIN;
    }

}
