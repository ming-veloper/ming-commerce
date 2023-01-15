package com.ming.mingcommerce.product;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductCrawl productCrawl;

    @GetMapping("/api/product-crawl")
    public ResponseEntity<?> crawl() throws IOException {
        productCrawl.crawl();
        Map<String, String> result = Map.of("message", "product successfully inserted");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
