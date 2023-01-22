package com.ming.mingcommerce.product;

import com.ming.mingcommerce.product.service.ProductCrawlService;
import com.ming.mingcommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {
    private final ProductCrawlService productCrawlService;
    private final ProductService productService;

    @GetMapping("/product-crawl")
    public ResponseEntity<?> crawl() throws IOException {
        Map<String, String> result = productCrawlService.crawl();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/products")
    public ResponseEntity<?> getProducts(@RequestParam int page, @RequestParam String category) {
        PageRequest pageRequest = PageRequest.of(page, 10);

        List<ProductModel> products = productService.getProducts(category, pageRequest);

        return new ResponseEntity<>(Map.of("result", products), HttpStatus.OK);
    }
}
