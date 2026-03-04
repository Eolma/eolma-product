package com.eolma.product.domain.service;

import com.eolma.common.exception.EolmaException;
import com.eolma.common.exception.ErrorType;
import com.eolma.product.domain.model.Category;
import com.eolma.product.domain.model.Product;
import com.eolma.product.domain.model.ProductStatus;
import com.eolma.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EolmaException(ErrorType.PRODUCT_NOT_FOUND));
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public Page<Product> findProducts(Category category, ProductStatus status, Pageable pageable) {
        if (category != null && status != null) {
            return productRepository.findByCategoryAndStatus(category, status, pageable);
        }
        if (category != null) {
            return productRepository.findByCategory(category, pageable);
        }
        if (status != null) {
            return productRepository.findByStatus(status, pageable);
        }
        return productRepository.findAll(pageable);
    }

    public Page<Product> findBySellerProducts(Long sellerId, Pageable pageable) {
        return productRepository.findBySellerId(sellerId, pageable);
    }
}
