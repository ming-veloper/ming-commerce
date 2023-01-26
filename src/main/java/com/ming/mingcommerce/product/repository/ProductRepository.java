package com.ming.mingcommerce.product.repository;

import com.ming.mingcommerce.product.entity.Category;
import com.ming.mingcommerce.product.entity.Product;
import com.ming.mingcommerce.product.exception.ProductException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByCategory(Category category, PageRequest pageable);

    default Product findProductById(String productId) throws ProductException {
        return findById(productId).orElseThrow(ProductException.ProductNotFoundException::new);
    }
}
