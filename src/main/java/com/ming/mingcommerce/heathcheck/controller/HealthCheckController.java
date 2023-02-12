package com.ming.mingcommerce.heathcheck.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/")
    String healthCheck() {
        return "The Service is up and running...";
    }

}
