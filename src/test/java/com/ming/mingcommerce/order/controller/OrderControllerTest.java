package com.ming.mingcommerce.order.controller;

import com.ming.mingcommerce.BaseControllerTest;
import com.ming.mingcommerce.cart.model.CartProductDTO;
import com.ming.mingcommerce.cart.model.CartProductRequest;
import com.ming.mingcommerce.cart.repository.CartRepository;
import com.ming.mingcommerce.cart.service.CartService;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.order.model.OrderRequest;
import com.ming.mingcommerce.order.service.OrderService;
import com.ming.mingcommerce.product.entity.Product;
import com.ming.mingcommerce.security.CurrentMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest extends BaseControllerTest {

    @Autowired
    OrderService orderService;

    @Autowired
    CartService cartService;

    @Autowired
    CartRepository cartRepository;


    @Test
    @DisplayName("장바구니의 상품을 주문한다")
    void order() throws Exception {
        Member member = saveMember();
        List<Product> products = saveProduct();
        List<OrderRequest> orderRequests = putInCart(products, member);
        String content = objectMapper.writeValueAsString(orderRequests);

        String token = jwtTokenUtil.issueToken(member).getAccessToken();
        mockMvc.perform(post("/api/orders/order")
                        .header(X_WWW_MING_AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("orderId").exists())
                .andExpect(jsonPath("amount").exists())
                .andDo(document("order",
                        requestHeaders(headerWithName(X_WWW_MING_AUTHORIZATION).description("인증 헤더")
                        ),
                        requestFields(
                                fieldWithPath("[].cartLindUuid").description("카트라인의 uuid 리스트")
                        ),
                        responseFields(
                                fieldWithPath("orderId").description("주문 아이디"),
                                fieldWithPath("amount").description("주문 총 합계"),
                                fieldWithPath("orderName").description("'[상품이름]외 [주문상품개수]건' 형식의 주문 이름")
                        )

                ))
        ;
    }

    // 인자로 주어진 상품 리스트를 장바구니에 담는다
    private List<OrderRequest> putInCart(List<Product> products, Member member) {
        CurrentMember currentMember = modelMapper.map(member, CurrentMember.class);
        products.forEach((product) -> {
                    CartProductRequest request = CartProductRequest.builder().productId(product.getProductId()).quantity(10L).build();
                    cartService.addProduct(currentMember, request);
                }
        );

        // 장바구니 상품 조회하여 카트라인 uuid 반환
        List<CartProductDTO> cartProductResponse = cartRepository.getCartProductResponse(member.getEmail());
        List<String> list = cartProductResponse.stream().map(CartProductDTO::getUuid).toList();

        return list.stream().map(OrderRequest::new).toList();
    }

}