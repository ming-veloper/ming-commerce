package com.ming.mingcommerce.mail;

import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.repository.MemberRepository;
import com.ming.mingcommerce.security.CurrentMember;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final MailConfig mailConfig;
    private final MemberRepository memberRepository;
    @Value("${spring.mail.username}")
    private String senderEmail;
    @Value("${ming.domain}")
    private String domainAddress;

    @Override
    @Transactional
    public String sendMail(String emailTo, CurrentMember currentMember) {
        // 메일 생성
        MailSender mailSender = mailConfig.getMailSender();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(senderEmail);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject("[밍커머스] 이메일 변경을 위한 인증 메일 입니다.");
        Member member = memberRepository.findMemberByEmail(currentMember.getEmail());

        String authenticationLink = domainAddress + "/api/members" + "?token=" + member.getEmailCheckToken() + "&newEmail=" + emailTo;
        mailMessage.setText("[밍커머스 인증하기]" + authenticationLink);
        mailSender.send(mailMessage);

        return "success";
    }

}
