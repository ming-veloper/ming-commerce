package com.ming.mingcommerce.product.service;

import com.ming.mingcommerce.product.ProductModel;
import com.ming.mingcommerce.product.entity.Category;
import com.ming.mingcommerce.product.entity.CategoryName;
import com.ming.mingcommerce.product.entity.Product;
import com.ming.mingcommerce.product.repository.CategoryRepository;
import com.ming.mingcommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;

    public List<ProductModel> getProducts(String category, PageRequest pageRequest) {
        Category ctg = categoryRepository.findByCategoryName(CategoryName.valueOf(category));
        List<Product> all = productRepository.findByCategory(ctg, pageRequest);
        return all.stream().map(p -> modelMapper.map(p, ProductModel.class)).toList();
    }
}
