package com.ming.mingcommerce.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductDetail {
    private String orderName;
    private Double totalAmount;
    private LocalDateTime createDate;
    private List<ProductDetail> productDetailList;

    public OrderProductDetail(OrderDetail orderDetail, List<ProductDetail> productDetailList) {
        this.orderName = orderDetail.getOrderName();
        this.totalAmount = orderDetail.getTotalAmount();
        this.createDate = orderDetail.getCreateDate();
        this.productDetailList = productDetailList;
    }
}
