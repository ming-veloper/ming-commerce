package com.ming.mingcommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MingCommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MingCommerceApplication.class, args);
    }

}
