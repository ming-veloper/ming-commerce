package com.ming.mingcommerce.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    MailSender getMailSender(Environment env) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(mailUsername(env));
        mailSender.setPassword(mailPassword(env));
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        return mailSender;
    }

    @Bean
    String mailUsername(Environment env) {
        String username = env.getProperty("spring.mail.username");
        if (username == null) username = System.getenv("MING_MAIL_SENDER_EMAIL");
        return username;
    }

    @Bean
    String mailPassword(Environment env) {
        String password = env.getProperty("spring.mail.password");
        if (password == null) password = System.getenv("MING_MAIL_SENDER_PASSWORD");
        return password;
    }
}
