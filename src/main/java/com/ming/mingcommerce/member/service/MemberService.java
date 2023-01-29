package com.ming.mingcommerce.member.service;

import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.exception.MemberException;
import com.ming.mingcommerce.member.model.JwtTokenModel;
import com.ming.mingcommerce.member.model.MemberModel;
import com.ming.mingcommerce.member.model.RegisterRequest;
import com.ming.mingcommerce.member.model.RegisterResponse;
import com.ming.mingcommerce.member.repository.MemberRepository;
import com.ming.mingcommerce.security.CurrentMember;
import com.ming.mingcommerce.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        // check whether email duplicated
        String email = registerRequest.getEmail();
        if (isDuplicatedEmail(email)) {
            throw new MemberException.EmailDuplicatedException(format("Email duplicated. duplicated email=%s", email));
        }
        // encrypt password
        String encryptedPassword = passwordEncoder.encode(registerRequest.getPassword());
        registerRequest.setPassword(encryptedPassword);

        // save member
        Member member = modelMapper.map(registerRequest, Member.class);
        // 권한 설정
        setRole(email, member);

        // 멤버 저장
        memberRepository.saveAndFlush(member);

        // issue token
        JwtTokenModel tokenModel = jwtTokenUtil.issueToken(member);


        return new RegisterResponse(tokenModel.getAccessToken(), tokenModel.getRefreshToken());
    }

    private void setRole(String email, Member member) {
        if (Objects.equals(email, adminEmail)) {
            member.setAdminRole();
        }
    }

    /**
     * 이메일 중복 여부를 체크한다.
     *
     * @param email 이메일
     * @return 중복되었다면 true 를 반환하고, 중복이 아니라면 false 를 반환한다.
     */
    public boolean isDuplicatedEmail(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        return member.isPresent();
    }

    public MemberModel getMemberInfo(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof CurrentMember currentMember)) {
            throw new IllegalArgumentException();
        }

        String email = currentMember.getEmail();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException.MemberEmailNotFoundException("Email cannot found: " + email));
        return modelMapper.map(member, MemberModel.class);
    }
}
