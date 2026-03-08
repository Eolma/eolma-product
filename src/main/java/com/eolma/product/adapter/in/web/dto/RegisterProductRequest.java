package com.eolma.product.adapter.in.web.dto;

import com.eolma.product.domain.model.Category;
import com.eolma.product.domain.model.ConditionGrade;
import com.eolma.product.domain.model.EndType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RegisterProductRequest(
        @NotBlank @Size(max = 200) String title,
        String description,
        @NotNull Category category,
        @NotNull ConditionGrade conditionGrade,
        @NotNull @Positive Long startingPrice,
        Long instantPrice,
        Integer instantBuyLockPercent,
        Long reservePrice,
        Long minBidUnit,
        @NotNull EndType endType,
        @Positive Integer durationHours,
        @Positive Integer maxBidCount,
        List<String> imageUrls
) {}
