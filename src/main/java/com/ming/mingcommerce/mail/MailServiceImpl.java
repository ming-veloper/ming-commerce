package com.ming.mingcommerce.mail;

import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.repository.MemberRepository;
import com.ming.mingcommerce.security.CurrentMember;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final MailSender mailSender;
    private final MemberRepository memberRepository;
    private final String mailUsername;
    private final String domainAddress;

    @Override
    public String sendMail(String emailTo, CurrentMember currentMember) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailUsername);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject("[밍커머스] 이메일 변경을 위한 인증 메일 입니다.");
        Member member = memberRepository.findMemberByEmail(currentMember.getEmail());
        String authenticationLink = domainAddress + "/update-user" + "?token=" + member.getEmailCheckToken() + "&email=" + emailTo;
        mailMessage.setText("[밍커머스 인증하기]" + authenticationLink);
        mailSender.send(mailMessage);

        return "success";
    }

}
