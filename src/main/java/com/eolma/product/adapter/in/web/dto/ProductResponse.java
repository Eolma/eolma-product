package com.eolma.product.adapter.in.web.dto;

import com.eolma.product.domain.model.Product;

import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
        Long id,
        Long sellerId,
        String title,
        String description,
        String category,
        String conditionGrade,
        Long startingPrice,
        Long instantPrice,
        Long reservePrice,
        Long minBidUnit,
        String endType,
        String endValue,
        String status,
        List<String> imageUrls,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSellerId(),
                product.getTitle(),
                product.getDescription(),
                product.getCategory().name(),
                product.getConditionGrade().name(),
                product.getStartingPrice(),
                product.getInstantPrice(),
                product.getReservePrice(),
                product.getMinBidUnit(),
                product.getEndType().name(),
                product.getEndValue(),
                product.getStatus().name(),
                product.getImageUrls(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
