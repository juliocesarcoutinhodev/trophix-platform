package com.trophix.api.offers.application.ports.in;

import com.trophix.api.offers.model.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Returns only the active offers, optionally filtered by category.
 */
public interface GetPublicOffersUseCase {

    Page<Offer> getPublicOffers(String category, Pageable pageable);
}
