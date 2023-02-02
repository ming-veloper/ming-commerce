package com.ming.mingcommerce.cart.model;

import com.ming.mingcommerce.cart.vo.CartLine;
import com.ming.mingcommerce.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartProductDTO {
    private String productId;
    private Double price;
    private Long quantity;
    private String productName;
    private String thumbnailImageUrl;
    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    public static CartProductDTO of(CartLine cartLine, Product product) {
        return CartProductDTO.builder()
                .productId(cartLine.getProductId())
                .quantity(cartLine.getQuantity())
                .createdDate(cartLine.getCreatedDate())
                .modifiedDate(cartLine.getModifiedDate())
                .price(product.getPrice())
                .productName(product.getProductName())
                .thumbnailImageUrl(product.getThumbnailImageUrl())
                .build();
    }
}
