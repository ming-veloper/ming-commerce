package com.ming.mingcommerce.product.repository;

import com.ming.mingcommerce.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
}
