package com.trophix.api.offers.infrastructure.adapter.in.mapper;

import com.trophix.api.offers.application.ports.in.ManageOffersUseCase;
import com.trophix.api.offers.infrastructure.adapter.in.dto.CreateOfferRequest;
import com.trophix.api.offers.infrastructure.adapter.in.dto.OfferResponse;
import com.trophix.api.offers.infrastructure.adapter.in.dto.UpdateOfferRequest;
import com.trophix.api.offers.model.Offer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OfferWebMapper {

    OfferResponse toResponse(Offer offer);

    ManageOffersUseCase.CreateOfferCommand toCreateCommand(CreateOfferRequest request);

    ManageOffersUseCase.UpdateOfferCommand toUpdateCommand(UpdateOfferRequest request);
}
