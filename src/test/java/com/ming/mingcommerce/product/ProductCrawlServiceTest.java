package com.ming.mingcommerce.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ming.mingcommerce.product.service.ProductCrawlService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("test")

class ProductCrawlServiceTest {
    @MockBean
    ProductCrawlService productCrawlService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("상품 데이터를 크롤링한다")
    void crawl() throws IOException {
        Map<String, String> message = Map.of("message", "product successfully inserted");

        Mockito.when(productCrawlService.crawl()).thenReturn(message);

        Map<String, String> result = productCrawlService.crawl();
        Assertions.assertThat(result.get("message")).isEqualTo("product successfully inserted");
    }

}