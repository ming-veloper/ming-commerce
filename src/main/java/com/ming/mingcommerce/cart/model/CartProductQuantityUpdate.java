package com.ming.mingcommerce.cart.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartProductQuantityUpdate {
    private String productId;
    private Long quantity;
}
