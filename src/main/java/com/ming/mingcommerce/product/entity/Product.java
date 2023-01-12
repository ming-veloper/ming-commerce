package com.ming.mingcommerce.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;

    private String productName;
    private String thumbnailImageUrl;
    private Integer price;
    private String description;
    @ElementCollection
    private List<String> productImageUrl;

    @OneToOne
    private Category category;
}
