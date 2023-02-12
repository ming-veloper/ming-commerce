package com.ming.mingcommerce.cart.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 장바구니 안의 상품 총 주문 금액 계산 조회에 쓸 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartLineDTO {
    private String uuid;
    private Long quantity;
    private Double price;
    private String productName;
}
