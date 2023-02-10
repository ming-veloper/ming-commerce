package com.ming.mingcommerce.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderResponse {
    private String orderId;
    private Double amount;
}
