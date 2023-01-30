package com.ming.mingcommerce.cart.controller;

import com.ming.mingcommerce.BaseControllerTest;
import com.ming.mingcommerce.cart.model.CartProductRequest;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.entity.Role;
import com.ming.mingcommerce.member.repository.MemberRepository;
import com.ming.mingcommerce.member.service.MemberService;
import com.ming.mingcommerce.product.entity.Category;
import com.ming.mingcommerce.product.entity.CategoryName;
import com.ming.mingcommerce.product.entity.Product;
import com.ming.mingcommerce.product.repository.CategoryRepository;
import com.ming.mingcommerce.product.repository.ProductRepository;
import com.ming.mingcommerce.util.JwtTokenUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
class CartControllerTest extends BaseControllerTest {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    MemberService memberService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("장바구니에 상품을 추가한다")
    void addProduct() throws Exception {
        Member member = saveMember();
        saveProduct();

        Product product = productRepository.findAll().stream().findFirst().get();

        CartProductRequest request = CartProductRequest.builder()
                .productId(product.getProductId())
                .quantity(10L).build();
        String data = objectMapper.writeValueAsString(request);
        String token = jwtTokenUtil.issueToken(member).getAccessToken();

        mockMvc.perform(post("/api/carts")
                        .header("X-WWW-MING-AUTHORIZATION", token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(data))

                .andExpect(status().isOk())
                .andExpect(jsonPath("cartLineNumber").exists())
                .andDo(document("add-product-to-cart",
                        requestHeaders(
                                headerWithName("X-WWW-MING-AUTHORIZATION").description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("productId").description("상품 고유 id"),
                                fieldWithPath("quantity").description("상품 수량")
                        ),
                        responseFields(
                                fieldWithPath("cartLineNumber").description("장바구니에 담긴 상품의 수")
                        )
                        )
                );
    }

    private Member saveMember() {
        return Member.builder()
                .memberName("tester")
                .role(Role.USER)
                .email("tester@ming.com")
                .password("tester123!").build();
    }

    private void saveProduct() {
        Category category = new Category(CategoryName.DAIRY_EGGS);
        categoryRepository.save(category);

        for (int i = 1; i < 3; i++) {
            Product product = Product.builder()
                    .category(category)
                    .productName("신선 달걀 " + i + "구")
                    .price(7.89d)
                    .thumbnailImageUrl("http://helloworld.com/egg.png")
                    .description("신선 신선").build();

            productRepository.save(product);
        }
    }

    @AfterEach
    public void afterEach() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        memberRepository.deleteAll();
    }

}