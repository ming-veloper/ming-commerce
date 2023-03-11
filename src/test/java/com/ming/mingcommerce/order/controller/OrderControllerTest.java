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
    @DisplayName("장바구니의 상품을 주문한다")
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
                        requestHeaders(headerWithName(X_WWW_MING_AUTHORIZATION).description("인증 헤더")
                        ),
                        requestFields(
                                fieldWithPath("cartLineUuidList.[]").description("카트라인의 uuid 리스트")
                        ),
                        responseFields(
                                fieldWithPath("orderId").description("주문 아이디"),
                                fieldWithPath("amount").description("주문 총 합계"),
                                fieldWithPath("orderName").description("'[상품이름]외 [주문상품개수]건' 형식의 주문 이름"),
                                fieldWithPath("orderThumbnailUrl").description("주문 썸네일 이미지 URL. 사용자 주문 조회 요청이 아닐 시 null 값이다.")
                        )

                ))
        ;
    }

    @Test
    @DisplayName("주문 아이디에 해당하는 주문을 조회한다")
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
                                fieldWithPath("orderThumbnailUrl").description("주문 썸네일 이미지 URL. 사용자 주문 조회 요청이 아닐 시 null 값이다.")
                        )
                ))


        ;
    }

    @Test
    @DisplayName("주문 상세를 조회한다")
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
                                headerWithName(X_WWW_MING_AUTHORIZATION).description("인증 헤더")
                        ),
                        queryParameters(
                                parameterWithName("orderId").description("주문 아이디")
                        ),
                        responseFields(
                                fieldWithPath("[].productId").description("상품 아이디"),
                                fieldWithPath("[].productName").description("상품명"),
                                fieldWithPath("[].thumbnailImageUrl").description("상품 썸네일 URL"),
                                fieldWithPath("[].price").description("상품 가격"),
                                fieldWithPath("[].quantity").description("상품 주문 수량")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("사용자의 주문을 조회한다")
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
                                headerWithName(X_WWW_MING_AUTHORIZATION).description("인증헤더")
                        ),
                        responseFields(
                                fieldWithPath("totalPages").description("총 페이지"),
                                fieldWithPath("first").description("첫 번재 페이지인지 여부"),
                                fieldWithPath("last").description("마지막 페이지인지 여부"),
                                fieldWithPath("totalElements").description("총 주문 갯수"),
                                fieldWithPath("content[].orderId").description("주문 아이디"),
                                fieldWithPath("content[].totalAmount").description("총 결제 금액"),
                                fieldWithPath("content[].orderName").description("주문 이름"),
                                fieldWithPath("content[].updatedDate").description("주문일시"),
                                fieldWithPath("content[].thumbnailImageUrl").description("주문 썸네일 이미지 URL")
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

    private OrderResponse orderAndPay(Member member) throws JsonProcessingException {
        // 상품과 멤버 저장 후 카트에 상품을 담아 주문한다
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
                "KRW", amount, "카드");
        when(paymentApprovalApi.processPay(any(PaymentApprovalRequest.class))).thenReturn(response);
        // when
        paymentService.pay(request, modelMapper.map(member, CurrentMember.class));

        return orderResponse;

    }
}