package com.ming.mingcommerce.cart.vo;

import com.ming.mingcommerce.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartLine {
    @Column(nullable = false, updatable = false)
    private String uuid;
    private String productId;
    private Double price;
    private Long quantity;
    private boolean deleted;

    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    public static CartLine createCartLine(Product product, Long quantity) {
        return CartLine.builder()
                // TODO using shorter uuid
                .uuid(UUID.randomUUID().toString())
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

    // 이전에 삭제했던 상품을 다시 담는다
    public void putBack() {
        this.deleted = false;
    }
}
