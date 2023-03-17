package com.ming.mingcommerce.mail;

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
    private final String mailUsername;
    private final String domainAddress;

    @Override
    public String sendMail(String encodedToken, String emailTo) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailUsername);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject("[밍커머스] 이메일 변경을 위한 인증 메일 입니다.");

        // 이메일 인증 호출을 위한 프론트 API
        String authenticationLink = domainAddress + "/update-user" + "?token=" + encodedToken;
        mailMessage.setText("[밍커머스 인증하기]" + authenticationLink);
        mailSender.send(mailMessage);

        return "success";
    }

}
