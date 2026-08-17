package com.trophix.api.offers.application.usecases;

import com.trophix.api.offers.application.ports.in.OfferAnalyticsUseCase;
import com.trophix.api.offers.application.ports.out.OfferRepository;
import com.trophix.api.offers.model.Offer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class OfferAnalyticsUseCaseImpl implements OfferAnalyticsUseCase {

    private final OfferRepository offerRepository;

    @Override
    @Transactional
    public void trackClick(UUID offerId) {
        offerRepository.incrementClickCount(offerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Offer> getTopOffers(int limit) {
        return offerRepository.findTopByClickCount(limit);
    }
}
