package com.trophix.api.offers.application.ports.out;

import com.trophix.api.offers.model.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Driven port: persistence for {@link Offer}.
 */
public interface OfferRepository {

    Offer save(Offer offer);

    Optional<Offer> findById(UUID offerId);

    /** Active offers only, optionally filtered by category, newest first. */
    Page<Offer> findPublicOffers(String category, Pageable pageable);

    /** All offers (including inactive), optionally filtered by category, newest first. */
    Page<Offer> findAllFiltered(String category, Pageable pageable);

    void delete(Offer offer);
}
