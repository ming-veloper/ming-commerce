package com.ming.mingcommerce.mail;

import com.ming.mingcommerce.security.CurrentMember;

public interface MailService {
    String sendMail(String emailTo, CurrentMember currentMember);
}
