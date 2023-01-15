package com.ming.mingcommerce.member.entity;

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
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;
    private String email;
    private String password;
    private String memberName;
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

}
