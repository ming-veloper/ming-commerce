package com.ming.mingcommerce.order.model;

import com.ming.mingcommerce.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private String orderId;
    private Double amount;
    private String orderName;
    private OrderStatus orderStatus;
}
