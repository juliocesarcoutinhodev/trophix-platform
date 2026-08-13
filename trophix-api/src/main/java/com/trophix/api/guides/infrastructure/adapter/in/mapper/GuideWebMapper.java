package com.trophix.api.guides.infrastructure.adapter.in.mapper;

import com.trophix.api.guides.application.ports.in.SubmitGuideUseCase;
import com.trophix.api.guides.application.ports.in.VoteGuideUseCase;
import com.trophix.api.guides.infrastructure.adapter.in.dto.GuideResponse;
import com.trophix.api.guides.infrastructure.adapter.in.dto.MessageResponse;
import com.trophix.api.guides.infrastructure.adapter.in.dto.SubmitGuideRequest;
import com.trophix.api.guides.infrastructure.adapter.in.dto.VoteResponse;
import com.trophix.api.guides.model.Guide;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

/**
 * Web mapping between HTTP DTOs and the guides application layer.
 * The controller only orchestrates: mapper.toCommand() -> use case -> mapper.toResponse().
 */
@Mapper(componentModel = "spring")
public interface GuideWebMapper {

    @Mapping(target = "trophyId", ignore = true)
    @Mapping(target = "gameId", source = "gameId")
    SubmitGuideUseCase.SubmitGuideCommand toGameGuideCommand(SubmitGuideRequest request, UUID gameId);

    @Mapping(target = "trophyId", source = "trophyId")
    @Mapping(target = "gameId", ignore = true)
    SubmitGuideUseCase.SubmitGuideCommand toTrophyGuideCommand(SubmitGuideRequest request, UUID trophyId);

    GuideResponse toGuideResponse(Guide guide);

    VoteResponse toVoteResponse(VoteGuideUseCase.VoteResult result);

    default MessageResponse toMessageResponse(String message) {
        return new MessageResponse(message);
    }
}
