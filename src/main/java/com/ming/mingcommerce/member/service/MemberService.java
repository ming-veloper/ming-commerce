package com.ming.mingcommerce.member.service;

import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.exception.MemberException;
import com.ming.mingcommerce.member.model.JwtTokenModel;
import com.ming.mingcommerce.member.model.RegisterRequest;
import com.ming.mingcommerce.member.model.RegisterResponse;
import com.ming.mingcommerce.member.repository.MemberRepository;
import com.ming.mingcommerce.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        memberRepository.save(member);

        // issue token
        JwtTokenModel tokenModel = jwtTokenUtil.issueToken(email, "USER");

        return new RegisterResponse(tokenModel.getAccessToken(), tokenModel.getRefreshToken());
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
}
