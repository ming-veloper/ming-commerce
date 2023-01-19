package com.ming.mingcommerce.product;

import com.ming.mingcommerce.BaseControllerTest;
import com.ming.mingcommerce.product.entity.Product;
import com.ming.mingcommerce.product.repository.ProductRepository;
import com.ming.mingcommerce.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ProductControllerTest extends BaseControllerTest {


    @MockBean
    ProductCrawler productCrawler;
    @MockBean
    ProductRepository productRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Value("${admin.email}")
    String adminEmail;


    @BeforeEach
    void setup() throws IOException {
        when(productCrawler.getProducts())
                .thenReturn(List.of(Product.builder()
                        .build()));
    }

    @Test
    @DisplayName("상품 데이터를 크롤링하여 DB 에 삽입하는 테스트")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void productCrawling_Role_ADMIN() throws Exception {
        String adminToken = jwtTokenUtil.issueToken(adminEmail).getAccessToken();
        mockMvc.perform(get("/api/product-crawl").header("X-WWW-MING-AUTHORIZATION", adminToken))
                .andExpect(status().isOk())
                .andDo(document("insert-product",
                        requestHeaders(headerWithName("X-WWW-MING-AUTHORIZATION").description("권한이 ADMIN 인 토큰"))
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("product successfully inserted"))
        ;
    }

    @Test
    @DisplayName("권한이 USER 이기 때문에 상품 데이터 삽입 api 호출시 403 에러")
    @WithMockUser(authorities = "ROLE_USER")
    void productCrawling_Role_USER() throws Exception {
        mockMvc.perform(get("/api/product-crawl"))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }
}