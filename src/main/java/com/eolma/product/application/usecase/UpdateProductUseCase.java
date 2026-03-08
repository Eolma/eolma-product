package com.eolma.product.application.usecase;

import com.eolma.product.adapter.in.web.dto.ProductResponse;
import com.eolma.product.adapter.in.web.dto.UpdateProductRequest;
import com.eolma.product.domain.model.Product;
import com.eolma.product.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UpdateProductUseCase {

    private final ProductService productService;

    @Transactional
    public ProductResponse execute(String sellerId, Long productId, UpdateProductRequest request) {
        Product product = productService.findById(productId);
        product.validateOwnership(sellerId);
        product.update(
                request.title(),
                request.description(),
                request.category(),
                request.conditionGrade(),
                request.startingPrice(),
                request.instantPrice(),
                request.instantBuyLockPercent(),
                request.reservePrice(),
                request.minBidUnit(),
                request.endType(),
                request.durationHours(),
                request.maxBidCount(),
                request.imageUrls()
        );
        Product saved = productService.save(product);
        return ProductResponse.from(saved);
    }

    @Transactional
    public void delete(String sellerId, Long productId) {
        Product product = productService.findById(productId);
        product.validateOwnership(sellerId);
        product.validateModifiable();
        productService.delete(productId);
    }
}
