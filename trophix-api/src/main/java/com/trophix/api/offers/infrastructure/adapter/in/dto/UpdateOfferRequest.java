package com.trophix.api.offers.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateOfferRequest(
        @NotBlank String title,
        @NotBlank String imageUrl,
        @NotNull BigDecimal originalPrice,
        @NotNull BigDecimal discountPrice,
        @NotBlank String storeName,
        @NotBlank String affiliateLink,
        @NotBlank String category,
        boolean isFlashDeal,
        boolean isActive) {
}
