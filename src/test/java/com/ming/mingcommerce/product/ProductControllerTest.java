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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
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

    @Test
    @DisplayName("상품 상세를 조회한다")
    void getProductDetail() throws Exception {
        Product product = productRepository.findAll().stream().findFirst().get();
        String productId = product.getProductId();
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['result'].productId").exists())
                .andExpect(jsonPath("$['result'].price").exists())
                .andExpect(jsonPath("$['result'].thumbnailImageUrl").exists())

                .andDo(document("get-product-detail",
                        pathParameters(
                                parameterWithName("productId").description("상품 아이디")
                        ),
                        responseFields(
                                fieldWithPath("result.productId").description("상품의 고유값. UUID 형식"),
                                fieldWithPath("result.productName").description("상품명"),
                                fieldWithPath("result.thumbnailImageUrl").description("상품 썸네일 url"),
                                fieldWithPath("result.productImageUrlList[]").description("상품 이미지 url"),
                                fieldWithPath("result.description").description("상품 상세 설명"),
                                fieldWithPath("result.price").description("상품의 가격"),
                                fieldWithPath("result.category").description("상품의 카테고리"),
                                fieldWithPath("result.createdDate").description("상품 생성일"),
                                fieldWithPath("result.modifiedDate").description("상품 수정일"),
                                fieldWithPath("result.category.createdDate").description("상품의 생성일"),
                                fieldWithPath("result.category.modifiedDate").description("상품의 수정일"),
                                fieldWithPath("result.category.categoryId").description("상품의 카테고리 id"),
                                fieldWithPath("result.category.categoryName").description("상품의 카테고리 이름")
                        )
                ))
        ;
    }
}
