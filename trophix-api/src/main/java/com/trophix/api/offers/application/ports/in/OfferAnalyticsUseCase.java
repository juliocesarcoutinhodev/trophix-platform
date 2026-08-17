package com.trophix.api.offers.application.ports.in;

import com.trophix.api.offers.model.Offer;

import java.util.List;
import java.util.UUID;

/**
 * Click analytics for the affiliate offers (LootBox).
 */
public interface OfferAnalyticsUseCase {

    /** Atomically increments the offer click counter (no read-modify-write race). */
    void trackClick(UUID offerId);

    /** Top offers ordered by click count (desc), limited. */
    List<Offer> getTopOffers(int limit);
}
