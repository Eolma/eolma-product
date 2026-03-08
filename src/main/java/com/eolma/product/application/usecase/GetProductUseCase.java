package com.eolma.product.application.usecase;

import com.eolma.common.dto.PageResponse;
import com.eolma.product.adapter.in.web.dto.ProductResponse;
import com.eolma.product.domain.model.Category;
import com.eolma.product.domain.model.Product;
import com.eolma.product.domain.model.ProductStatus;
import com.eolma.product.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetProductUseCase {

    private final ProductService productService;

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = productService.findById(id);
        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> findProducts(Category category, ProductStatus status,
                                                       Pageable pageable) {
        Page<Product> page = productService.findProducts(category, status, pageable);
        return PageResponse.of(
                page.getContent().stream().map(ProductResponse::from).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> findMyProducts(String sellerId, Pageable pageable) {
        Page<Product> page = productService.findBySellerProducts(sellerId, pageable);
        return PageResponse.of(
                page.getContent().stream().map(ProductResponse::from).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
