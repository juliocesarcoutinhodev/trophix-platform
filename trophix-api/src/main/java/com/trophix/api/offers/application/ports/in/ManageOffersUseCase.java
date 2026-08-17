package com.trophix.api.offers.application.ports.in;

import com.trophix.api.offers.model.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Admin operations over offers (including inactive ones).
 */
public interface ManageOffersUseCase {

    Offer create(CreateOfferCommand command);

    Offer update(UUID offerId, UpdateOfferCommand command);

    void delete(UUID offerId);

    Page<Offer> listAll(String category, Pageable pageable);

    record CreateOfferCommand(String title, String imageUrl,
                              BigDecimal originalPrice, BigDecimal discountPrice,
                              String storeName, String affiliateLink, String category,
                              boolean isFlashDeal) {
    }

    record UpdateOfferCommand(String title, String imageUrl,
                              BigDecimal originalPrice, BigDecimal discountPrice,
                              String storeName, String affiliateLink, String category,
                              boolean isFlashDeal, boolean isActive) {
    }
}
