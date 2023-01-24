package com.ming.mingcommerce.product;

import com.ming.mingcommerce.product.entity.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductModel {
    private String uuid;
    private String productName;
    private Double price;
    private String thumbnailImageUrl;
    private Category category;
}
