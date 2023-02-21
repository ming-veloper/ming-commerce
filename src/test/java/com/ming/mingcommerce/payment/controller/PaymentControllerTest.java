package com.ming.mingcommerce.payment.controller;

import com.ming.mingcommerce.BaseControllerTest;
import com.ming.mingcommerce.member.entity.Member;
import com.ming.mingcommerce.order.entity.Order;
import com.ming.mingcommerce.payment.PaymentApprovalApi;
import com.ming.mingcommerce.payment.model.PaymentApprovalRequest;
import com.ming.mingcommerce.payment.model.PaymentApprovalResponse;
import com.ming.mingcommerce.payment.service.PaymentService;
import com.ming.mingcommerce.security.CurrentMember;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static java.time.LocalDateTime.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentControllerTest extends BaseControllerTest {
    @MockBean
    PaymentService paymentService;

    @MockBean
    PaymentApprovalApi paymentApprovalApi;

    @Autowired
    PaymentController paymentController;


    @Test
    @DisplayName("토스 결제 승인을 진행한다")
    @WithMockUser(username = "tester", roles = "USER")
    void tossPayApproval() throws Exception {
        Member member = saveMember();
        String orderName = "테스트 상품 외 1건";
        Order savedOrder = orderRepository.save(Order.builder().member(member).orderName(orderName).totalAmount(2d).build());

        String paymentKey = Base64.encodeBase64String("test payment key".getBytes());
        PaymentApprovalRequest request = PaymentApprovalRequest.builder()
                .paymentKey(paymentKey)
                .orderId(savedOrder.getOrderId())
                .amount(2d)
                .build();

        PaymentApprovalResponse response = new PaymentApprovalResponse(savedOrder.getOrderId(), orderName,
                now().toString(),
                now().toString(),
                "KRW",
                1000d,
                "카드");
        CurrentMember currentMember = modelMapper.map(member, CurrentMember.class);

        when(paymentService.pay(any(PaymentApprovalRequest.class), any(CurrentMember.class)))
                .thenReturn(response);
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/payment/pay")
                        .header(X_WWW_MING_AUTHORIZATION, jwtTokenUtil.issueToken(member).getAccessToken())
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("orderId").exists())
                .andExpect(jsonPath("orderName").exists())
                .andExpect(jsonPath("requestedAt").exists())

                .andDo(document("payment-approval",
                        requestHeaders(
                                headerWithName(X_WWW_MING_AUTHORIZATION).description("밍커머스 인증 토큰")
                        ), requestFields(
                                fieldWithPath("paymentKey").description("결제의 키 값. 최대 길이는 200자."),
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("amount").description("결제할 금액")
                        ), responseFields(
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("orderName").description("주문 이름"),
                                fieldWithPath("requestedAt").description("결제 요청 시간"),
                                fieldWithPath("approvedAt").description("결제 승인 시간"),
                                fieldWithPath("currency").description("결제할 때 사용한 통화 단위. 원화인 KRW 만 사용"),
                                fieldWithPath("totalAmount").description("총 결제 금액"),
                                fieldWithPath("method").description("결제할 때 사용한 결제 수단. 카드, 가상계좌, 간편결제, 휴대폰, 계좌이체, 상품권(문화상품권, 도서문화상품권, 게임문화상품권) 중 하나")
                        )
                ));
    }
}