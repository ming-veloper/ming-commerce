package com.ming.mingcommerce.order.model;

import com.ming.mingcommerce.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail {
    private String orderName;
    private Double totalAmount;
    private LocalDateTime createDate;

    public OrderDetail(Order order) {
        this(order.getOrderName(), order.getTotalAmount(), order.getCreatedDate());
    }
}
