package com.ming.mingcommerce.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class MyOrderModel {
    private String orderId;
    private String orderName;
    private Double totalAmount;
    private String thumbnailImageUrl;
    private LocalDateTime updatedDate;
}
