package com.trophix.api.offers.infrastructure.adapter.out;

import com.trophix.api.offers.application.ports.out.OfferRepository;
import com.trophix.api.offers.model.Offer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
class OfferJpaAdapter implements OfferRepository {

    private static final int MAX_TOP_LIMIT = 100;

    private final OfferSpringDataRepository springDataRepository;
    private final OfferMapper mapper;

    @Override
    @Transactional
    public Offer save(Offer offer) {
        return mapper.toDomain(springDataRepository.save(mapper.toEntity(offer)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Offer> findById(UUID offerId) {
        return springDataRepository.findById(offerId).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Offer> findPublicOffers(String category, Pageable pageable) {
        return springDataRepository.findPublicOffers(category, pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Offer> findAllFiltered(String category, Pageable pageable) {
        return springDataRepository.findAllFiltered(category, pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void delete(Offer offer) {
        springDataRepository.deleteById(offer.id());
    }

    @Override
    @Transactional
    public void incrementClickCount(UUID offerId) {
        springDataRepository.incrementClickCount(offerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Offer> findTopByClickCount(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, MAX_TOP_LIMIT));
        return springDataRepository
                .findAllByOrderByClickCountDesc(PageRequest.of(0, safeLimit))
                .getContent()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
