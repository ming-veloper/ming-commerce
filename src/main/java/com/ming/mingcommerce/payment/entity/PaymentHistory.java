package com.ming.mingcommerce.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
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
