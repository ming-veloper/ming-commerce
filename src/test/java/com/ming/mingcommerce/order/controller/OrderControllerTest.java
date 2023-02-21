package com.ming.mingcommerce.order.controller;

import com.ming.mingcommerce.BaseControllerTest;
import com.ming.mingcommerce.cart.model.CartProductDTO;
import com.ming.mingcommerce.cart.model.CartProductRequest;
import com.ming.mingcommerce.cart.repository.CartRepository;
import com.ming.mingcommerce.cart.service.CartService;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.order.entity.Order;
import com.ming.mingcommerce.order.model.OrderRequest;
import com.ming.mingcommerce.order.respository.OrderRepository;
import com.ming.mingcommerce.order.service.OrderService;
import com.ming.mingcommerce.product.entity.Product;
import com.ming.mingcommerce.security.CurrentMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest extends BaseControllerTest {

    @Autowired
    OrderService orderService;

    @Autowired
    CartService cartService;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    OrderRepository orderRepository;

    @BeforeEach
    void beforeEach() {
        cartRepository.deleteAll();
        orderRepository.deleteAll();
        memberRepository.deleteAll();
    }


    @Test
    @DisplayName("장바구니의 상품을 주문한다")
    void order() throws Exception {
        Member member = saveMember();
        List<Product> products = saveProduct();
        OrderRequest orderRequests = putInCart(products, member);
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
                                fieldWithPath("cartLineUuidList.[]").description("카트라인의 uuid 리스트")
                        ),
                        responseFields(
                                fieldWithPath("orderId").description("주문 아이디"),
                                fieldWithPath("amount").description("주문 총 합계"),
                                fieldWithPath("orderName").description("'[상품이름]외 [주문상품개수]건' 형식의 주문 이름"),
                                fieldWithPath("orderStatus").description("주문 상태. 주문 완료시 PENDING 이며, 결제까지 완료되면 COMPLETE 으로 바뀐다")
                        )

                ))
        ;
    }

    @Test
    @DisplayName("주문을 조회한다")
    void getOrderById() throws Exception {
        Member member = saveMember();
        String orderName = "테스트 상품 외 1건";
        Order savedOrder = orderRepository.save(Order.builder().member(member).orderName(orderName).totalAmount(2d).build());

        mockMvc.perform(get("/api/orders/order")
                        .header(X_WWW_MING_AUTHORIZATION, jwtTokenUtil.issueToken(member).getAccessToken())
                        .param("orderId", savedOrder.getOrderId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("orderId").exists())
                .andExpect(jsonPath("orderName").exists())
                .andExpect(jsonPath("amount").exists())

                .andDo(document("get-order-by-id",
                        requestHeaders(
                                headerWithName(X_WWW_MING_AUTHORIZATION).description("밍커머스 인증헤더")
                        ), queryParameters(
                                parameterWithName("orderId").description("주문 ID")
                        ), responseFields(
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("orderName").description("주문 이름. 2건 이상시 첫번째 상품 이름에 '외 n 건' 을 붙여 저장한다"),
                                fieldWithPath("amount").description("총 결제 금액"),
                                fieldWithPath("orderStatus").description("주문 상태. 주문 완료시 PENDING 이며, 결제까지 완료되면 COMPLETE 으로 바뀐다")
                        )
                ))


        ;
    }

    // 인자로 주어진 상품 리스트를 장바구니에 담는다
    private OrderRequest putInCart(List<Product> products, Member member) {
        CurrentMember currentMember = modelMapper.map(member, CurrentMember.class);
        products.forEach((product) -> {
                    CartProductRequest request = CartProductRequest.builder().productId(product.getProductId()).quantity(10L).build();
                    cartService.addProduct(currentMember, request);
                }
        );

        // 장바구니 상품 조회하여 카트라인 uuid 반환
        List<CartProductDTO> cartProductResponse = cartRepository.getCartProductResponse(member.getEmail());
        List<String> list = cartProductResponse.stream().map(CartProductDTO::getUuid).toList();

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.addCartLineUuid(list);

        return orderRequest;
    }

}