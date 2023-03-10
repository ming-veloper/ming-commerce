package com.ming.mingcommerce.product;


import com.ming.mingcommerce.product.entity.Category;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ProductRequest {
    private String productName;

    private String thumbnailImageUrl;
    private Double price;

    private String description;
    private List<String> productImageUrlList;

    private Category category;

}
