package com.trophix.api.offers.model;

import com.trophix.api.shared.domain.UuidV7;
import com.trophix.api.shared.exception.BusinessException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

/**
 * A discounted product offer (LootBox). Pure Java, immutable. Prices are kept
 * in {@link BigDecimal}; the discount percentage is always derived from the
 * two prices so it can never diverge.
 */
public record Offer(
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

    public Offer {
        if (title == null || title.isBlank()) {
            throw new BusinessException("Título é obrigatório.");
        }
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new BusinessException("Imagem é obrigatória.");
        }
        if (storeName == null || storeName.isBlank()) {
            throw new BusinessException("Loja é obrigatória.");
        }
        if (affiliateLink == null || affiliateLink.isBlank()) {
            throw new BusinessException("Link de afiliado é obrigatório.");
        }
        if (category == null || category.isBlank()) {
            throw new BusinessException("Categoria é obrigatória.");
        }
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("O preço original deve ser maior que zero.");
        }
        if (discountPrice == null || discountPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("O preço com desconto não pode ser negativo.");
        }
        if (discountPrice.compareTo(originalPrice) > 0) {
            throw new BusinessException("O preço com desconto não pode ser maior que o preço original.");
        }
        // Percentual é sempre derivado dos preços — nunca pode divergir.
        discountPercentage = calculateDiscountPercentage(originalPrice, discountPrice);
    }

    public static Offer create(String title, String imageUrl,
                               BigDecimal originalPrice, BigDecimal discountPrice,
                               String storeName, String affiliateLink, String category,
                               boolean isFlashDeal) {
        Instant now = Instant.now();
        return new Offer(UuidV7.generate(), title, imageUrl, originalPrice, discountPrice,
                null, storeName, affiliateLink, category, isFlashDeal, true, now, now);
    }

    /** Returns a copy with the admin-editable fields replaced (recomputes the percentage). */
    public Offer updated(String title, String imageUrl,
                         BigDecimal originalPrice, BigDecimal discountPrice,
                         String storeName, String affiliateLink, String category,
                         boolean isFlashDeal, boolean isActive) {
        return new Offer(id, title, imageUrl, originalPrice, discountPrice,
                null, storeName, affiliateLink, category, isFlashDeal, isActive, createdAt, Instant.now());
    }

    /** ((original - discount) / original) * 100, rounded half-up. */
    static Integer calculateDiscountPercentage(BigDecimal originalPrice, BigDecimal discountPrice) {
        BigDecimal savings = originalPrice.subtract(discountPrice);
        return savings.multiply(BigDecimal.valueOf(100))
                .divide(originalPrice, 0, RoundingMode.HALF_UP)
                .intValue();
    }
}
