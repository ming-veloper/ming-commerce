package com.ming.mingcommerce.payment.model;

import lombok.Getter;

@Getter
public class PaymentApprovalRequest {
    private String paymentKey;
    private Double amount;
    private String orderId;
}
