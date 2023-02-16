package com.ming.mingcommerce.payment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentApprovalResponse {
    private String orderId;
    private String orderName;
    // 토스에서 반환하는 날짜 타입은 ISO 8601 형식
    private String requestedAt;
    private String approvedAt;
    private String currency;
    private Double totalAmount;
    // 간편결제 등 결제 방법
    private String method;
}
