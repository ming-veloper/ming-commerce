package com.ming.mingcommerce.order.model;

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

    public OrderDetail(String orderName, Double totalAmount, LocalDateTime createDate) {
        this.orderName = orderName;
        this.totalAmount = totalAmount;
        this.createDate = createDate;
    }
}
