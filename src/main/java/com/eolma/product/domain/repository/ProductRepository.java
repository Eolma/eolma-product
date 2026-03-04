package com.eolma.product.domain.repository;

import com.eolma.product.domain.model.Category;
import com.eolma.product.domain.model.Product;
import com.eolma.product.domain.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    void deleteById(Long id);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    Page<Product> findByCategory(Category category, Pageable pageable);

    Page<Product> findByCategoryAndStatus(Category category, ProductStatus status, Pageable pageable);

    Page<Product> findBySellerId(Long sellerId, Pageable pageable);

    Page<Product> findAll(Pageable pageable);
}
