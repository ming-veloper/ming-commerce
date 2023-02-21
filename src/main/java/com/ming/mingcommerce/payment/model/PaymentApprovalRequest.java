package com.ming.mingcommerce.payment.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentApprovalRequest {
    private String paymentKey;
    private Double amount;
    private String orderId;
}
