package com.ming.mingcommerce.product.controller;

import com.ming.mingcommerce.product.ProductModel;
import com.ming.mingcommerce.product.model.ProductDetailDTO;
import com.ming.mingcommerce.product.service.ProductCrawlService;
import com.ming.mingcommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 상품 조회
     *
     * @param page
     * @param category
     * @return 상품 목록을 10개씩 반환한다.
     */
    @GetMapping("/products")
    public ResponseEntity<?> getProducts(@RequestParam int page, @RequestParam String category) {
        PageRequest pageRequest = PageRequest.of(page, 10);

        List<ProductModel> products = productService.getProducts(category, pageRequest);

        return new ResponseEntity<>(Map.of("result", products), HttpStatus.OK);
    }

    /**
     * 상품 상세 조회
     *
     * @param productId 상품 id
     * @return 상품 id 에 해당하는 상품 상세를 반환한다.
     */
    @GetMapping("/products/{productId}")
    public ResponseEntity<?> getProductDetail(@PathVariable String productId) {
        ProductDetailDTO productDetail = productService.getProductDetail(productId);

        return new ResponseEntity<>(Map.of("result", productDetail), HttpStatus.OK);
    }
}
