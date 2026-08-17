package com.trophix.api.offers.infrastructure.adapter.in;

import com.trophix.api.offers.application.ports.in.ManageOffersUseCase;
import com.trophix.api.offers.application.ports.in.OfferAnalyticsUseCase;
import com.trophix.api.offers.infrastructure.adapter.in.dto.CreateOfferRequest;
import com.trophix.api.offers.infrastructure.adapter.in.dto.OfferResponse;
import com.trophix.api.offers.infrastructure.adapter.in.dto.UpdateOfferRequest;
import com.trophix.api.offers.infrastructure.adapter.in.mapper.OfferWebMapper;
import com.trophix.api.offers.model.Offer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Admin CRUD over offers (including inactive ones). Protected by ROLE_ADMIN.
 */
@RestController
@RequestMapping("/api/admin/offers")
@RequiredArgsConstructor
class OfferAdminController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final ManageOffersUseCase manageOffersUseCase;
    private final OfferAnalyticsUseCase offerAnalyticsUseCase;
    private final OfferWebMapper offerWebMapper;

    @GetMapping
    public ResponseEntity<Page<OfferResponse>> listAll(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<OfferResponse> offers = manageOffersUseCase
                .listAll(category, PageRequest.of(Math.max(page, 0), Math.min(size, 100)))
                .map(offerWebMapper::toResponse);
        return ResponseEntity.ok(offers);
    }

    @GetMapping("/analytics/top-clicked")
    public ResponseEntity<List<OfferResponse>> topClicked(@RequestParam(defaultValue = "5") int limit) {
        List<OfferResponse> top = offerAnalyticsUseCase.getTopOffers(limit).stream()
                .map(offerWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(top);
    }

    @PostMapping
    public ResponseEntity<OfferResponse> create(@Valid @RequestBody CreateOfferRequest request) {
        Offer offer = manageOffersUseCase.create(offerWebMapper.toCreateCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(offerWebMapper.toResponse(offer));
    }

    @PutMapping("/{offerId}")
    public ResponseEntity<OfferResponse> update(@PathVariable UUID offerId,
                                                @Valid @RequestBody UpdateOfferRequest request) {
        Offer offer = manageOffersUseCase.update(offerId, offerWebMapper.toUpdateCommand(request));
        return ResponseEntity.ok(offerWebMapper.toResponse(offer));
    }

    @DeleteMapping("/{offerId}")
    public ResponseEntity<Void> delete(@PathVariable UUID offerId) {
        manageOffersUseCase.delete(offerId);
        return ResponseEntity.noContent().build();
    }
}
