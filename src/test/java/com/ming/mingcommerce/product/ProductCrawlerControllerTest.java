package com.ming.mingcommerce.product;

import com.ming.mingcommerce.BaseControllerTest;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.entity.Role;
import com.ming.mingcommerce.product.entity.Category;
import com.ming.mingcommerce.product.entity.CategoryName;
import com.ming.mingcommerce.product.entity.Product;
import com.ming.mingcommerce.product.repository.CategoryRepository;
import com.ming.mingcommerce.product.repository.ProductRepository;
import com.ming.mingcommerce.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class ProductCrawlerControllerTest extends BaseControllerTest {


    @MockBean
    ProductCrawler productCrawler;
    @MockBean
    ProductRepository productRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Value("${admin.email}")
    String adminEmail;
    @Autowired
    private CategoryRepository categoryRepository;


    @BeforeEach
    void setup() throws IOException {
        when(productCrawler.getProducts())
                .thenReturn(List.of(Product.builder()
                        .build()));

        when(productRepository.save(Mockito.any(Product.class)))
                .thenAnswer(i -> i.getArguments()[0]);
        Category category = new Category(CategoryName.DAIRY_EGGS);
        categoryRepository.save(category);

        for (int i = 1; i < 20; i++) {
            Product product = Product.builder()
                    .category(category)
                    .productName("?????? ?????? " + i + "???")
                    .price(7.89d)
                    .thumbnailImageUrl("http://helloworld.com/egg.png")
                    .description("?????? ??????").build();

            productRepository.save(product);
        }
    }

    @Test
    @DisplayName("?????? ???????????? ??????????????? DB ??? ???????????? ?????????")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void productCrawling_Role_ADMIN() throws Exception {
        Member member = Member.builder()
                .email(adminEmail)
                .uuid(UUID.randomUUID().toString())
                .role(Role.ADMIN).build();

        String adminToken = jwtTokenUtil.issueToken(member).getAccessToken();
        mockMvc.perform(get("/api/product-crawl").header("X-WWW-MING-AUTHORIZATION", adminToken))
                .andExpect(status().isOk())
                .andDo(document("insert-product",
                        requestHeaders(headerWithName("X-WWW-MING-AUTHORIZATION").description("????????? ADMIN ??? ??????"))
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("product successfully inserted"))
        ;
    }

    @Test
    @DisplayName("????????? USER ?????? ????????? ?????? ????????? ?????? api ????????? 403 ??????")
    @WithMockUser(authorities = "ROLE_USER")
    void productCrawling_Role_USER() throws Exception {
        mockMvc.perform(get("/api/product-crawl"))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

}