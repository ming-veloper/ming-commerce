package com.ming.mingcommerce.cart.vo;

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

    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    public void plusQuantity(Long quantity) {
        if (quantity > 1) {
            this.quantity += quantity;
            return;
        }
        this.quantity += 1;
    }
}
