package com.ming.mingcommerce.payment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class PayApprovalResponse {
    private String orderId;
    private String orderName;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private String currency;
    private Double totalAmount;

    // 간편결제 등 결제 방법
    private String method;
}
