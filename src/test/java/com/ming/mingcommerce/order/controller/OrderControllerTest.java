package com.ming.mingcommerce.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ming.mingcommerce.BaseControllerTest;
import com.ming.mingcommerce.cart.entity.Cart;
import com.ming.mingcommerce.cart.model.CartProductDTO;
import com.ming.mingcommerce.cart.model.CartProductRequest;
import com.ming.mingcommerce.cart.repository.CartRepository;
import com.ming.mingcommerce.cart.service.CartService;
import com.ming.mingcommerce.cart.vo.CartLine;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.order.entity.Order;
import com.ming.mingcommerce.order.model.OrderRequest;
import com.ming.mingcommerce.order.model.OrderResponse;
import com.ming.mingcommerce.order.respository.OrderRepository;
import com.ming.mingcommerce.order.service.OrderService;
import com.ming.mingcommerce.payment.PaymentApprovalApi;
import com.ming.mingcommerce.payment.model.PaymentApprovalRequest;
import com.ming.mingcommerce.payment.model.PaymentApprovalResponse;
import com.ming.mingcommerce.payment.service.PaymentService;
import com.ming.mingcommerce.product.entity.Product;
import com.ming.mingcommerce.security.CurrentMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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

    @MockBean
    PaymentApprovalApi paymentApprovalApi;

    @Autowired
    OrderService orderService;

    @Autowired
    CartService cartService;

    @Autowired
    PaymentService paymentService;

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
    @DisplayName("??????????????? ????????? ????????????")
    void order() throws Exception {
        Member member = saveMember();
        List<Product> products = saveProduct();
        OrderRequest orderRequest = putInCart(products, member);
        String content = objectMapper.writeValueAsString(orderRequest);

        String token = jwtTokenUtil.issueToken(member).getAccessToken();
        mockMvc.perform(post("/api/orders/order")
                        .header(X_WWW_MING_AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("orderId").exists())
                .andExpect(jsonPath("amount").exists())
                .andExpect(jsonPath("orderName").exists())
                .andExpect(jsonPath("orderThumbnailUrl").exists())
                .andDo(document("order",
                        requestHeaders(headerWithName(X_WWW_MING_AUTHORIZATION).description("?????? ??????")
                        ),
                        requestFields(
                                fieldWithPath("cartLineUuidList.[]").description("??????????????? uuid ?????????")
                        ),
                        responseFields(
                                fieldWithPath("orderId").description("?????? ?????????"),
                                fieldWithPath("amount").description("?????? ??? ??????"),
                                fieldWithPath("orderName").description("'[????????????]??? [??????????????????]???' ????????? ?????? ??????"),
                                fieldWithPath("orderThumbnailUrl").description("?????? ????????? ????????? URL. ????????? ?????? ?????? ????????? ?????? ??? null ?????????.")
                        )

                ))
        ;
    }

    @Test
    @DisplayName("?????? ???????????? ???????????? ????????? ????????????")
    void getOrderById() throws Exception {
        Member member = saveMember();
        String orderName = "????????? ?????? ??? 1???";
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
                                headerWithName(X_WWW_MING_AUTHORIZATION).description("???????????? ????????????")
                        ), queryParameters(
                                parameterWithName("orderId").description("?????? ID")
                        ), responseFields(
                                fieldWithPath("orderId").description("?????? ID"),
                                fieldWithPath("orderName").description("?????? ??????. 2??? ????????? ????????? ?????? ????????? '??? n ???' ??? ?????? ????????????"),
                                fieldWithPath("amount").description("??? ?????? ??????"),
                                fieldWithPath("orderThumbnailUrl").description("?????? ????????? ????????? URL. ????????? ?????? ?????? ????????? ?????? ??? null ?????????.")
                        )
                ))


        ;
    }

    @Test
    @DisplayName("?????? ????????? ????????????")
    void orderDetail() throws Exception {
        // given
        Member member = saveMember();
        OrderResponse orderResponse = orderAndPay(member);

        mockMvc.perform(get("/api/orders/order-detail")
                        .queryParam("orderId", orderResponse.getOrderId())
                        .header(X_WWW_MING_AUTHORIZATION, jwtTokenUtil.issueToken(member).getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].productId").exists())
                .andExpect(jsonPath("$[*].productName").exists())
                .andExpect(jsonPath("$[*].thumbnailImageUrl").exists())
                .andExpect(jsonPath("$[*].price").exists())
                .andExpect(jsonPath("$[*].quantity").exists())
                .andDo(document("get-order-detail",
                        requestHeaders(
                                headerWithName(X_WWW_MING_AUTHORIZATION).description("?????? ??????")
                        ),
                        queryParameters(
                                parameterWithName("orderId").description("?????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("[].productId").description("?????? ?????????"),
                                fieldWithPath("[].productName").description("?????????"),
                                fieldWithPath("[].thumbnailImageUrl").description("?????? ????????? URL"),
                                fieldWithPath("[].price").description("?????? ??????"),
                                fieldWithPath("[].quantity").description("?????? ?????? ??????")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("???????????? ????????? ????????????")
    public void myOrder() throws Exception {
        Member member = saveMember();
        orderAndPay(member);

        mockMvc.perform(get("/api/orders/my-order")
                        .header(X_WWW_MING_AUTHORIZATION, jwtTokenUtil.issueToken(member).getAccessToken())
                        .queryParam("page", "0")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("totalPages").exists())
                .andExpect(jsonPath("content").exists())
                .andExpect(jsonPath("first").exists())
                .andExpect(jsonPath("last").exists())

                .andDo(document("get-my-order",
                        requestHeaders(
                                headerWithName(X_WWW_MING_AUTHORIZATION).description("????????????")
                        ),
                        responseFields(
                                fieldWithPath("totalPages").description("??? ?????????"),
                                fieldWithPath("first").description("??? ?????? ??????????????? ??????"),
                                fieldWithPath("last").description("????????? ??????????????? ??????"),
                                fieldWithPath("totalElements").description("??? ?????? ??????"),
                                fieldWithPath("content[].orderId").description("?????? ?????????"),
                                fieldWithPath("content[].totalAmount").description("??? ?????? ??????"),
                                fieldWithPath("content[].orderName").description("?????? ??????"),
                                fieldWithPath("content[].updatedDate").description("????????????"),
                                fieldWithPath("content[].thumbnailImageUrl").description("?????? ????????? ????????? URL")
                        )
                ))
        ;
    }

    // ????????? ????????? ?????? ???????????? ??????????????? ?????????
    private OrderRequest putInCart(List<Product> products, Member member) {
        CurrentMember currentMember = modelMapper.map(member, CurrentMember.class);
        products.forEach((product) -> {
                    CartProductRequest request = CartProductRequest.builder().productId(product.getProductId()).quantity(10L).build();
                    cartService.addProduct(currentMember, request);
                }
        );

        // ???????????? ?????? ???????????? ???????????? uuid ??????
        List<CartProductDTO> cartProductResponse = cartRepository.getCartProductResponse(member.getEmail());
        List<String> list = cartProductResponse.stream().map(CartProductDTO::getUuid).toList();

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.addCartLineUuid(list);

        return orderRequest;
    }

    private OrderResponse orderAndPay(Member member) throws JsonProcessingException {
        // ????????? ?????? ?????? ??? ????????? ????????? ?????? ????????????
        List<Product> products = saveProduct();
        Cart cart = new Cart("test-cart-id", List.of(CartLine.createCartLine(products.get(0), 10L)), member);
        cartRepository.save(cart);

        OrderResponse orderResponse = orderService.order(member, new OrderRequest(List.of(cart.getCartLines().get(0).getUuid())));
        String orderId = orderResponse.getOrderId();
        Double amount = orderResponse.getAmount();
        PaymentApprovalRequest request = PaymentApprovalRequest.builder()
                .paymentKey("test-payment-key")
                .amount(amount)
                .orderId(orderId).build();
        PaymentApprovalResponse response = new PaymentApprovalResponse(orderId,
                "testOrder",
                "2023-02-21", "2023-02-21",
                "KRW", amount, "??????");
        when(paymentApprovalApi.processPay(any(PaymentApprovalRequest.class))).thenReturn(response);
        // when
        paymentService.pay(request, modelMapper.map(member, CurrentMember.class));

        return orderResponse;

    }
}