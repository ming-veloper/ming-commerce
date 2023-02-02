package com.ming.mingcommerce.product.entity;

import com.ming.mingcommerce.product.repository.CategoryRepository;
import com.ming.mingcommerce.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductTest {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Test
    @DisplayName("상품을 생성한다")
    void createProduct() {
        Category category = new Category(CategoryName.SNACKS_SWEETS);
        Category savedCategory = categoryRepository.save(category);

        String productName = "팝팝 팝콘 7kg";
        Product product = Product
                .builder()
                .productName(productName)
                .productImageUrlList(List.of("https://m.media-amazon.com/images/I/81rUkJ7owXL._SL1500_.jpg", "https://m.media-amazon.com/images/I/913UOn3IttL._SL1500_.jpg"))
                .description("Start your day with tasty pastry crust and sweet strawberry flavor; A delicious morning treat that’s great for kids and adults")
                .category(category)
                .price(13.9d)
                .thumbnailImageUrl("https://m.media-amazon.com/images/I/81HnwYDxSlL._AC_UL640_FMwebp_QL65_.jpg")
                .build();

        Product savedProduct = productRepository.save(product);
        // 카테고리 생성 확인
        assertThat(savedCategory.getCategoryId()).isNotNull();

        // 상품 생성 확인
        assertThat(savedProduct.getProductId()).isNotNull();
        assertThat(savedProduct.getProductName()).isEqualTo(productName);
        assertThat(savedProduct.getProductImageUrlList().size()).isEqualTo(2);
    }
}