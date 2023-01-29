package com.ming.mingcommerce.cart.model;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartProductRequest {
    @NotNull
    private String productId;

    // 수량은 최소 한개 이상이어야 한다.
    @Min(value = 1L, message = "상품은 1개 이상 담아야 합니다.")
    private Long quantity;
}
