package com.ming.mingcommerce.member.entity;

import com.ming.mingcommerce.config.BaseTimeEntity;
import com.ming.mingcommerce.member.exception.MemberException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    private LocalDateTime emailTokenGeneratedAt;

    public void setAdminRole() {
        this.role = Role.ADMIN;
    }

    /**
     * 토큰은 1시간에 한번만 생성할 수 있다.
     */
    public String generateEmailAuthenticationToken() {
        if (emailTokenGeneratedAt != null && !(emailTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1)))) {
            throw new MemberException.ExceedEmailTokenIssue("인증메일은 1시간마다 발급할 수 있습니다.");
        }
        this.emailTokenGeneratedAt = LocalDateTime.now();
        this.emailCheckToken = UUID.randomUUID().toString();
        return this.emailCheckToken;
    }

    public Member changeEmail(String email) {
        this.email = email;
        deleteToken();
        return this;
    }

    private void deleteToken() {
        this.emailCheckToken = null;
    }

}
