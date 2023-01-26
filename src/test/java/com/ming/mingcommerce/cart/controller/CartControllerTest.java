package com.ming.mingcommerce.cart.controller;

import com.ming.mingcommerce.BaseControllerTest;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.entity.Role;
import com.ming.mingcommerce.member.service.MemberService;
import com.ming.mingcommerce.product.entity.Category;
import com.ming.mingcommerce.product.entity.CategoryName;
import com.ming.mingcommerce.product.entity.Product;
import com.ming.mingcommerce.product.repository.CategoryRepository;
import com.ming.mingcommerce.product.repository.ProductRepository;
import com.ming.mingcommerce.util.JwtTokenUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    @DisplayName("장바구니에 상품을 추가한다")
    void addProduct() throws Exception {
        Member member = saveMember();
        saveProduct();

        Product product = productRepository.findAll().stream().findFirst().get();
        String token = jwtTokenUtil.issueToken(member).getAccessToken();
        mockMvc.perform(get("/api/carts")
                        .header("X-WWW-MING-AUTHORIZATION", token)
                        .param("productId", product.getProductId())
                        .param("quantity", "7"))

                .andExpect(status().isOk())
                .andDo(document("add-product-to-cart",
                        requestHeaders(
                        headerWithName("X-WWW-MING-AUTHORIZATION").description("액세스 토큰")),
                        queryParameters(
                                parameterWithName("productId").description("상품 고유 id"),
                                parameterWithName("quantity").description("상품 수량")
                        ))
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
}