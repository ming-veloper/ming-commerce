package com.ming.mingcommerce.cart.controller;

import com.ming.mingcommerce.BaseControllerTest;
import com.ming.mingcommerce.cart.model.CartProductDeleteRequest;
import com.ming.mingcommerce.cart.model.CartProductQuantityUpdate;
import com.ming.mingcommerce.cart.model.CartProductRequest;
import com.ming.mingcommerce.cart.service.CartService;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.member.service.MemberService;
import com.ming.mingcommerce.product.entity.Product;
import com.ming.mingcommerce.product.repository.CategoryRepository;
import com.ming.mingcommerce.product.repository.ProductRepository;
import com.ming.mingcommerce.security.CurrentMember;
import com.ming.mingcommerce.util.JwtTokenUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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
    CartService cartService;

    @Test
    @DisplayName("??????????????? ????????? ????????????")
    void getCartProduct() throws Exception {
        // ?????? ????????? ?????? ??????
        Member member = saveMember();
        saveProduct();
        // ?????? ??????
        Product product = productRepository.findAll().stream().findFirst().get();
        // ??????????????? ?????? ??????
        String productId = product.getProductId();
        CartProductRequest request = CartProductRequest.builder().productId(productId).quantity(10L).build();

        String token = jwtTokenUtil.issueToken(member).getAccessToken();

        CurrentMember currentMember = modelMapper.map(member, CurrentMember.class);
        cartService.addProduct(currentMember, request);

        mockMvc.perform(get("/api/carts")
                        .header(X_WWW_MING_AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$['result'][0].uuid").exists())
                .andExpect(jsonPath("$['result'][0].productId").exists())
                .andExpect(jsonPath("$['result'][0].price").exists())
                .andExpect(jsonPath("$['result'][0].thumbnailImageUrl").exists())

                .andDo(document("get-cart-products",
                        requestHeaders(
                                headerWithName(X_WWW_MING_AUTHORIZATION).description("????????????")
                        ),
                        responseFields(
                                fieldWithPath("result[].uuid").description("???????????? ?????? ?????? id"),
                                fieldWithPath("result[].productId").description("?????? ?????? id"),
                                fieldWithPath("result[].thumbnailImageUrl").description("?????? ????????? url"),
                                fieldWithPath("result[].productName").description("????????? ??????"),
                                fieldWithPath("result[].price").description("????????? ??????"),
                                fieldWithPath("result[].quantity").description("????????? ??????"),
                                fieldWithPath("result[].createdDate").description("?????????"),
                                fieldWithPath("result[].modifiedDate").description("?????????")
                        )
                ))


        ;
    }

    @Test
    @DisplayName("??????????????? ????????? ????????????")
    void addProduct() throws Exception {
        Member member = saveMember();
        saveProduct();

        Product product = productRepository.findAll().stream().findFirst().get();

        CartProductRequest request = CartProductRequest.builder().productId(product.getProductId()).quantity(10L).build();
        String data = objectMapper.writeValueAsString(request);
        String token = jwtTokenUtil.issueToken(member).getAccessToken();

        mockMvc.perform(post("/api/carts").header(X_WWW_MING_AUTHORIZATION, token).contentType(MediaType.APPLICATION_JSON_VALUE).content(data))

                .andExpect(status().isOk()).andExpect(jsonPath("cartLineCount").exists()).andDo(document("add-product-to-cart", requestHeaders(headerWithName(X_WWW_MING_AUTHORIZATION).description("????????? ??????")), requestFields(fieldWithPath("productId").description("?????? ?????? id"), fieldWithPath("quantity").description("?????? ??????")), responseFields(fieldWithPath("cartLineCount").description("??????????????? ?????? ????????? ???"))));
    }

    @Test
    @DisplayName("??????????????? ?????? ????????? ????????????")
    void updateQuantity() throws Exception {
        // ?????? ????????? ?????? ??????
        Member member = saveMember();
        saveProduct();
        // ?????? ??????
        Product product = productRepository.findAll().stream().findFirst().get();
        // ??????????????? ?????? ??????
        String productId = product.getProductId();
        CartProductRequest request = CartProductRequest.builder().productId(productId).quantity(10L).build();

        String token = jwtTokenUtil.issueToken(member).getAccessToken();

        CurrentMember currentMember = modelMapper.map(member, CurrentMember.class);
        cartService.addProduct(currentMember, request);

        // ??????????????? ?????? ????????? ????????????
        CartProductQuantityUpdate update = CartProductQuantityUpdate.builder().productId(productId).quantity(7L).build();
        String data = objectMapper.writeValueAsString(update);

        mockMvc.perform(put("/api/carts")
                        .header(X_WWW_MING_AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(data)).andExpect(status().isOk())
                .andExpect(jsonPath("cartLineCount").exists())

                .andDo(document("update-cart-product-quantity",
                        requestHeaders(headerWithName(X_WWW_MING_AUTHORIZATION).description("????????? ??????")),
                        requestFields(fieldWithPath("productId").description("?????? ?????? id"), fieldWithPath("quantity").description("??????????????? ?????? ??????")),
                        responseFields(fieldWithPath("cartLineCount").description("??????????????? ?????? ????????? ???"))));
    }

    @Test
    @DisplayName("??????????????? ?????? ????????? ????????????")
    void deleteProduct() throws Exception {
        // ?????? ????????? ?????? ??????
        Member member = saveMember();
        // ?????? 3??? ??????
        saveProduct();
        // ????????? ?????? ??????
        Product product = productRepository.findAll().stream().findFirst().get();
        // ??????????????? ?????? ?????? ??????
        String productId = product.getProductId();
        CartProductRequest request = CartProductRequest.builder().productId(productId).quantity(10L).build();
        CurrentMember currentMember = modelMapper.map(member, CurrentMember.class);
        cartService.addProduct(currentMember, request);

        // ??????????????? ?????? ??????. ????????? ????????? ????????? ???????????????, ??? ?????? api ?????? ??? ????????? 0?????? ????????? ??????.
        CartProductDeleteRequest deleteRequest = CartProductDeleteRequest.builder().productId(productId).build();
        String data = objectMapper.writeValueAsString(deleteRequest);
        String token = jwtTokenUtil.issueToken(member).getAccessToken();

        mockMvc.perform(delete("/api/carts")
                        .header(X_WWW_MING_AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(data)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("cartLineCount").value(0))
                .andDo(print())
                .andDo(document("delete-cart-product",
                        requestHeaders(
                                headerWithName(X_WWW_MING_AUTHORIZATION).description("????????? ??????")
                        ),
                        requestFields(
                                fieldWithPath("productId").description("????????? ????????? ?????? id")
                        ),
                        responseFields(
                                fieldWithPath("cartLineCount").description("??????????????? ?????? ????????? ??????")
                        )
                ))

        ;

    }

}