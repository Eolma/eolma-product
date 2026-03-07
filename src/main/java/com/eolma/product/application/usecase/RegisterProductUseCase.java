package com.eolma.product.application.usecase;

import com.eolma.product.adapter.in.web.dto.RegisterProductRequest;
import com.eolma.product.domain.model.Product;
import com.eolma.product.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RegisterProductUseCase {

    private final ProductService productService;

    @Transactional
    public Product execute(Long sellerId, RegisterProductRequest request) {
        Product product = Product.builder()
                .sellerId(sellerId)
                .title(request.title())
                .description(request.description())
                .category(request.category())
                .conditionGrade(request.conditionGrade())
                .startingPrice(request.startingPrice())
                .instantPrice(request.instantPrice())
                .reservePrice(request.reservePrice())
                .minBidUnit(request.minBidUnit())
                .endType(request.endType())
                .durationHours(request.durationHours())
                .maxBidCount(request.maxBidCount())
                .imageUrls(request.imageUrls())
                .build();

        return productService.save(product);
    }
}
