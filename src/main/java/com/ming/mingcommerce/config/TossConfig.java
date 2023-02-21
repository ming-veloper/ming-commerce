package com.ming.mingcommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class TossConfig {
    @Bean
    String tossSecretKey(Environment env) {
        String secretKey = env.getProperty("toss.payments.secret-key");
        if (secretKey == null) secretKey = System.getenv("TOSS_SECRET_KEY");
        secretKey += ":";
        return secretKey;
    }
}
