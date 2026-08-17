package com.trophix.api.offers.application.usecases;

import com.trophix.api.offers.application.ports.in.GetPublicOffersUseCase;
import com.trophix.api.offers.application.ports.out.OfferRepository;
import com.trophix.api.offers.model.Offer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
class GetPublicOffersUseCaseImpl implements GetPublicOffersUseCase {

    private final OfferRepository offerRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Offer> getPublicOffers(String category, Pageable pageable) {
        return offerRepository.findPublicOffers(category, pageable);
    }
}
