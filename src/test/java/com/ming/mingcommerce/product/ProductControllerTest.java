package com.ming.mingcommerce.product;

import com.ming.mingcommerce.BaseControllerTest;
import com.ming.mingcommerce.product.entity.Category;
import com.ming.mingcommerce.product.entity.CategoryName;
import com.ming.mingcommerce.product.entity.Product;
import com.ming.mingcommerce.product.repository.CategoryRepository;
import com.ming.mingcommerce.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProductControllerTest extends BaseControllerTest {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @BeforeEach
    void setup() {
        Category category = new Category(CategoryName.DAIRY_EGGS);
        categoryRepository.save(category);

        // 상품 20 개 저장
        for (int i = 0; i < 20; i++) {
            Product product = Product.builder()
                    .category(category)
                    .productName("신선 달걀 " + i + "구")
                    .price(7.89d)
                    .thumbnailImageUrl("http://helloworld.com/egg.png")
                    .description("신선 신선").build();

            productRepository.save(product);
        }
    }

    @Test
    @DisplayName("카테고리별로 상품을 10개씩 조회한다 - DAIRY_EGGS")
    void getProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("page", "0").param("category", CategoryName.DAIRY_EGGS.name())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['result'][0].productId").exists())
                .andExpect(jsonPath("$['result'][0].price").exists())
                .andExpect(jsonPath("$['result'][0].thumbnailImageUrl").exists())

                .andDo(document("get-products",

                        queryParameters(
                                parameterWithName("page").description("조회할 페이지"),
                                parameterWithName("category").description("조회할 상품 카테고리")
                        ),
                        responseFields(
                                fieldWithPath("result[].productId").description("상품의 고유값. UUID 형식"),
                                fieldWithPath("result[].productName").description("상품명"),
                                fieldWithPath("result[].thumbnailImageUrl").description("상품 썸네일 url"),
                                fieldWithPath("result[].price").description("상품의 가격"),
                                fieldWithPath("result[].category").description("상품의 카테고리"),
                                fieldWithPath("result[].category.createdDate").description("상품의 생성일"),
                                fieldWithPath("result[].category.modifiedDate").description("상품의 수정일"),
                                fieldWithPath("result[].category.categoryId").description("상품의 카테고리 id"),
                                fieldWithPath("result[].category.categoryName").description("상품의 카테고리 이름")
                        )))
        ;

    }
}
