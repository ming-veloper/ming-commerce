package com.ming.mingcommerce;

import com.ming.mingcommerce.product.ProductCrawl;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class MingCommerceApplication {
    @Autowired
    ProductCrawl productCrawl;

    public static void main(String[] args) {
        SpringApplication.run(MingCommerceApplication.class, args);
    }

    @PostConstruct
    void saveProduct() throws IOException {
        productCrawl.crawl();
    }

}
