package com.ming.mingcommerce.mail;

public interface MailService {
    String sendMail(String encodedToken, String emailTo);
}
