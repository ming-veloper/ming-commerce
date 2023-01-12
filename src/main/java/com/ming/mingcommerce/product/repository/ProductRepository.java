package com.ming.mingcommerce.product.repository;

import com.ming.mingcommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}
