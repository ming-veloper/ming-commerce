package com.ming.mingcommerce.product.entity;

import com.ming.mingcommerce.config.BaseTimeEntity;
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
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;
    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String thumbnailImageUrl;
    private Double price;

    @Column(nullable = false)
    private String description;
    @ElementCollection
    private List<String> productImageUrl;

    @OneToOne
    @JoinColumn(nullable = false)
    private Category category;
}
