package com.trophix.api.offers.application.usecases;

import com.trophix.api.offers.application.ports.in.ManageOffersUseCase;
import com.trophix.api.offers.application.ports.out.OfferRepository;
import com.trophix.api.offers.model.Offer;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class ManageOffersUseCaseImpl implements ManageOffersUseCase {

    private static final String NOT_FOUND_MESSAGE = "Oferta não encontrada";

    private final OfferRepository offerRepository;

    @Override
    @Transactional
    public Offer create(CreateOfferCommand command) {
        Offer offer = Offer.create(
                command.title(), command.imageUrl(),
                command.originalPrice(), command.discountPrice(),
                command.storeName(), command.affiliateLink(), command.category(),
                command.isFlashDeal());
        return offerRepository.save(offer);
    }

    @Override
    @Transactional
    public Offer update(UUID offerId, UpdateOfferCommand command) {
        Offer existing = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE));
        Offer updated = existing.updated(
                command.title(), command.imageUrl(),
                command.originalPrice(), command.discountPrice(),
                command.storeName(), command.affiliateLink(), command.category(),
                command.isFlashDeal(), command.isActive());
        return offerRepository.save(updated);
    }

    @Override
    @Transactional
    public void delete(UUID offerId) {
        Offer existing = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE));
        offerRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Offer> listAll(String category, Pageable pageable) {
        return offerRepository.findAllFiltered(category, pageable);
    }
}
