package com.ming.mingcommerce.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetail {
    private String productId;
    private String productName;
    private String thumbnailImageUrl;
    private Double price;
    private Long quantity;
}
