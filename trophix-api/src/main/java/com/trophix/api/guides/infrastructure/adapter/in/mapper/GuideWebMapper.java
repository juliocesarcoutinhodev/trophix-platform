package com.trophix.api.guides.infrastructure.adapter.in.mapper;

import com.trophix.api.guides.application.ports.in.SubmitGuideUseCase;
import com.trophix.api.guides.application.ports.in.VoteGuideUseCase;
import com.trophix.api.guides.infrastructure.adapter.in.dto.GuideResponse;
import com.trophix.api.guides.infrastructure.adapter.in.dto.MessageResponse;
import com.trophix.api.guides.infrastructure.adapter.in.dto.SubmitGuideRequest;
import com.trophix.api.guides.infrastructure.adapter.in.dto.VoteResponse;
import com.trophix.api.guides.model.Guide;
import com.trophix.api.guides.model.GuideListItem;
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

    @Mapping(target = "gameName", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "authorName", ignore = true)
    @Mapping(target = "currentUserVoted", ignore = true)
    GuideResponse toGuideResponse(Guide guide);

    @Mapping(source = "guide.id", target = "id")
    @Mapping(source = "guide.trophyId", target = "trophyId")
    @Mapping(source = "guide.gameId", target = "gameId")
    @Mapping(source = "guide.authorId", target = "authorId")
    @Mapping(source = "guide.title", target = "title")
    @Mapping(source = "guide.description", target = "description")
    @Mapping(source = "guide.content", target = "content")
    @Mapping(source = "guide.videoUrl", target = "videoUrl")
    @Mapping(source = "guide.status", target = "status")
    @Mapping(source = "guide.upvotesCount", target = "upvotesCount")
    @Mapping(source = "guide.createdAt", target = "createdAt")
    @Mapping(source = "guide.updatedAt", target = "updatedAt")
    GuideResponse toGuideResponse(GuideListItem item);

    VoteResponse toVoteResponse(VoteGuideUseCase.VoteResult result);

    default MessageResponse toMessageResponse(String message) {
        return new MessageResponse(message);
    }
}
