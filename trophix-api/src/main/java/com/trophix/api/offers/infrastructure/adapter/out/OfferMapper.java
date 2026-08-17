package com.trophix.api.offers.infrastructure.adapter.out;

import com.trophix.api.offers.model.Offer;
import org.springframework.stereotype.Component;

@Component
class OfferMapper {

    Offer toDomain(OfferEntity entity) {
        return new Offer(
                entity.getId(),
                entity.getTitle(),
                entity.getImageUrl(),
                entity.getOriginalPrice(),
                entity.getDiscountPrice(),
                entity.getDiscountPercentage(),
                entity.getStoreName(),
                entity.getAffiliateLink(),
                entity.getCategory(),
                entity.isFlashDeal(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    OfferEntity toEntity(Offer offer) {
        OfferEntity entity = new OfferEntity();
        entity.setId(offer.id());
        entity.setTitle(offer.title());
        entity.setImageUrl(offer.imageUrl());
        entity.setOriginalPrice(offer.originalPrice());
        entity.setDiscountPrice(offer.discountPrice());
        entity.setDiscountPercentage(offer.discountPercentage());
        entity.setStoreName(offer.storeName());
        entity.setAffiliateLink(offer.affiliateLink());
        entity.setCategory(offer.category());
        entity.setFlashDeal(offer.isFlashDeal());
        entity.setActive(offer.isActive());
        entity.setCreatedAt(offer.createdAt());
        entity.setUpdatedAt(offer.updatedAt());
        return entity;
    }
}
