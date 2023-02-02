package com.ming.mingcommerce.product.model;

import com.ming.mingcommerce.product.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDetailDTO {
    private String productId;
    private String productName;

    private String thumbnailImageUrl;
    private Double price;

    private String description;
    private List<String> productImageUrl;

    private Category category;

    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
