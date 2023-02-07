package com.ming.mingcommerce.cart.model;

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
    private String uuid;
    private String productId;
    private Double price;
    private Long quantity;
    private String productName;
    private String thumbnailImageUrl;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
