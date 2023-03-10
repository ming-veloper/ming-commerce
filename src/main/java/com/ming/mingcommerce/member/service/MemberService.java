package com.ming.mingcommerce.member.service;

import com.ming.mingcommerce.mail.MailService;
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
    private final MailService mailService;

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
        // ?????? ??????
        setRole(email, member);

        // ?????? ??????
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
     * ????????? ?????? ????????? ????????????.
     *
     * @param email ?????????
     * @return ?????????????????? true ??? ????????????, ????????? ???????????? false ??? ????????????.
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

    @Transactional
    public JwtTokenModel changeEmail(String token, String newEmail) {
        Member member = memberRepository.findMemberByEmailCheckToken(token);
        Member changedMember = member.changeEmail(newEmail);
        return jwtTokenUtil.issueToken(changedMember);
    }

    @Transactional
    public void sendEmail(String emailTo, CurrentMember currentMember) {
        // ?????? ????????? ??????????????? ??????
        validateEmail(emailTo, currentMember);
        Member member = memberRepository.findMemberByEmail(currentMember.getEmail());
        member.generateEmailAuthenticationToken();
        mailService.sendMail(emailTo, currentMember);
    }

    private void validateEmail(String emailTo, CurrentMember currentMember) {
        if (Objects.equals(emailTo, currentMember.getEmail())) {
            throw new MemberException.CurrentlyInUseEmailException(emailTo + "??? ?????? ????????? ???????????? ????????????.");
        }
        memberRepository.findByEmail(emailTo).ifPresent((m) -> {
            throw new MemberException.EmailDuplicatedException("?????? ???????????? ??????????????????.");
        });
    }
}
