package com.ming.mingcommerce.order.model;

import com.ming.mingcommerce.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail {
    private String orderName;
    private Double totalAmount;
    private LocalDateTime createDate;
    private List<ProductDetail> productDetailList = new ArrayList<>();

    public OrderDetail(Order order) {
        this.orderName = order.getOrderName();
        this.totalAmount = order.getTotalAmount();
        this.createDate = order.getCreatedDate();
    }
}
