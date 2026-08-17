package com.trophix.api.offers.infrastructure.adapter.in;

import com.trophix.api.offers.application.ports.in.GetPublicOffersUseCase;
import com.trophix.api.offers.infrastructure.adapter.in.dto.OfferResponse;
import com.trophix.api.offers.infrastructure.adapter.in.mapper.OfferWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public offers feed: only active offers, optionally filtered by category.
 */
@RestController
@RequestMapping("/api/public/offers")
@RequiredArgsConstructor
class OfferPublicController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final GetPublicOffersUseCase getPublicOffersUseCase;
    private final OfferWebMapper offerWebMapper;

    @GetMapping
    public ResponseEntity<Page<OfferResponse>> getOffers(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<OfferResponse> offers = getPublicOffersUseCase
                .getPublicOffers(category, PageRequest.of(Math.max(page, 0), Math.min(size, 100)))
                .map(offerWebMapper::toResponse);
        return ResponseEntity.ok(offers);
    }
}
