package com.ming.mingcommerce.product;

import com.ming.mingcommerce.product.entity.Category;
import com.ming.mingcommerce.product.entity.CategoryName;
import com.ming.mingcommerce.product.entity.Product;
import com.ming.mingcommerce.product.repository.CategoryRepository;
import com.ming.mingcommerce.product.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TODO
 * 애플리케이션 초기화 후 하나의 상품 데이터 생성하여 DB 에 삽입.
 * 테스트성 데이터이며 크롤링 이후 본 파일 삭제 예정.
 */
@Component
public class PostConstructProductSaveBean {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @PostConstruct
    void saveProduct() {
        Category category = new Category(CategoryName.SNACKS_SWEETS);
        Category savedCategory = categoryRepository.save(category);

        String productName = "팝팝 팝콘 7kg";
        Product product = Product
                .builder()
                .productName(productName)
                .productImageUrl(List.of("https://m.media-amazon.com/images/I/81rUkJ7owXL._SL1500_.jpg", "https://m.media-amazon.com/images/I/913UOn3IttL._SL1500_.jpg"))
                .description("Start your day with tasty pastry crust and sweet strawberry flavor; A delicious morning treat that’s great for kids and adults")
                .category(category)
                .price((int) 13.9)
                .thumbnailImageUrl("https://m.media-amazon.com/images/I/81HnwYDxSlL._AC_UL640_FMwebp_QL65_.jpg")
                .build();

        Product savedProduct = productRepository.save(product);
    }
}
