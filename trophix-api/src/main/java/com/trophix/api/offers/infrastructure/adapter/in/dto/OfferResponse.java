package com.trophix.api.offers.infrastructure.adapter.in.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OfferResponse(
        UUID id,
        String title,
        String imageUrl,
        BigDecimal originalPrice,
        BigDecimal discountPrice,
        Integer discountPercentage,
        String storeName,
        String affiliateLink,
        String category,
        boolean isFlashDeal,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt) {
}
