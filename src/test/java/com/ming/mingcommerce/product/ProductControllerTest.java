package com.ming.mingcommerce.product;

import com.ming.mingcommerce.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO 외부 api (아마존 상품 페이지) 에 의존적인 테스트. 해결 방안 찾아야 한다.
@SpringBootTest
class ProductControllerTest extends BaseControllerTest {

    @Test
    @DisplayName("상품 데이터를 크롤링하여 DB 에 삽입하는 테스트")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void productCrawling_Role_ADMIN() throws Exception {
        mockMvc.perform(get("/api/product-crawl"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("권한이 USER 이기 때문에 상품 데이터 삽입 api 호출시 403 에러")
    @WithMockUser(authorities = "ROLE_USER")
    void productCrawling_Role_USER() throws Exception {
        mockMvc.perform(get("/api/product-crawl"))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }
}