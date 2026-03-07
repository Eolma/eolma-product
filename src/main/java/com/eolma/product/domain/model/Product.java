package com.eolma.product.domain.model;

import com.eolma.common.exception.EolmaException;
import com.eolma.common.exception.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_grade", nullable = false, length = 20)
    private ConditionGrade conditionGrade;

    @Column(name = "starting_price", nullable = false)
    private Long startingPrice;

    @Column(name = "instant_price")
    private Long instantPrice;

    @Column(name = "reserve_price")
    private Long reservePrice;

    @Column(name = "min_bid_unit", nullable = false)
    private Long minBidUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "end_type", nullable = false, length = 20)
    private EndType endType;

    @Column(name = "duration_hours")
    private Integer durationHours;

    @Column(name = "max_bid_count")
    private Integer maxBidCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "image_urls", columnDefinition = "jsonb")
    private List<String> imageUrls;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Product(Long sellerId, String title, String description, Category category,
                   ConditionGrade conditionGrade, Long startingPrice, Long instantPrice,
                   Long reservePrice, Long minBidUnit, EndType endType,
                   Integer durationHours, Integer maxBidCount,
                   List<String> imageUrls) {
        this.sellerId = sellerId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.conditionGrade = conditionGrade;
        this.startingPrice = startingPrice;
        this.instantPrice = instantPrice;
        this.reservePrice = reservePrice;
        this.minBidUnit = minBidUnit != null ? minBidUnit : 1000L;
        this.endType = endType;
        this.durationHours = durationHours;
        this.maxBidCount = maxBidCount;
        this.status = ProductStatus.DRAFT;
        this.imageUrls = imageUrls;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String title, String description, Category category,
                       ConditionGrade conditionGrade, Long startingPrice, Long instantPrice,
                       Long reservePrice, Long minBidUnit, EndType endType,
                       Integer durationHours, Integer maxBidCount,
                       List<String> imageUrls) {
        validateModifiable();
        this.title = title;
        this.description = description;
        this.category = category;
        this.conditionGrade = conditionGrade;
        this.startingPrice = startingPrice;
        this.instantPrice = instantPrice;
        this.reservePrice = reservePrice;
        this.minBidUnit = minBidUnit;
        this.endType = endType;
        this.durationHours = durationHours;
        this.maxBidCount = maxBidCount;
        this.imageUrls = imageUrls;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        if (this.status != ProductStatus.DRAFT) {
            throw new EolmaException(ErrorType.INVALID_REQUEST,
                    "Only DRAFT products can be activated");
        }
        this.status = ProductStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeStatus(ProductStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status == ProductStatus.IN_AUCTION || this.status == ProductStatus.SOLD) {
            throw new EolmaException(ErrorType.INVALID_REQUEST,
                    "Cannot cancel product in current status: " + this.status);
        }
        this.status = ProductStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void validateOwnership(Long requestSellerId) {
        if (!this.sellerId.equals(requestSellerId)) {
            throw new EolmaException(ErrorType.FORBIDDEN,
                    "You don't have permission to modify this product");
        }
    }

    public void validateModifiable() {
        if (this.status != ProductStatus.DRAFT) {
            throw new EolmaException(ErrorType.INVALID_REQUEST,
                    "Only DRAFT products can be modified");
        }
    }

    public boolean isDraft() {
        return this.status == ProductStatus.DRAFT;
    }

    public boolean isOwnedBy(Long sellerId) {
        return this.sellerId.equals(sellerId);
    }
}
