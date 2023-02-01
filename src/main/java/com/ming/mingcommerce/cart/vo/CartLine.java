package com.ming.mingcommerce.cart.vo;

import com.ming.mingcommerce.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartLine {

    private String productId;
    private Double price;
    private Long quantity;
    private boolean deleted;

    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    public static CartLine createCartLine(Product product, Long quantity) {
        return CartLine.builder()
                .productId(product.getProductId())
                .price(product.getPrice())
                .quantity(quantity)
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .build();
    }

    public void plusQuantity(Long quantity) {
        this.quantity += quantity;
    }

    public void updateQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void delete() {
        this.deleted = true;
    }
}
