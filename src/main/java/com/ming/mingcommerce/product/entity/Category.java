package com.ming.mingcommerce.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String productCategoryId;

    @Enumerated(EnumType.STRING)
    private CategoryName categoryName;

    public Category(CategoryName categoryName) {
        this.categoryName = categoryName;
    }
}
