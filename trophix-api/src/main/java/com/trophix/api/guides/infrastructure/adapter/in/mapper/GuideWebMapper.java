package com.trophix.api.guides.infrastructure.adapter.in.mapper;

import com.trophix.api.guides.infrastructure.adapter.in.dto.GuideResponse;
import com.trophix.api.guides.infrastructure.adapter.in.dto.MessageResponse;
import com.trophix.api.guides.infrastructure.adapter.in.dto.VoteResponse;
import com.trophix.api.guides.model.Guide;
import org.springframework.stereotype.Component;

@Component
public class GuideWebMapper {

    public GuideResponse toGuideResponse(Guide guide) {
        return new GuideResponse(
                guide.id(),
                guide.trophyId(),
                guide.gameId(),
                guide.authorId(),
                guide.content(),
                guide.videoUrl(),
                guide.status().name(),
                guide.upvotesCount(),
                guide.createdAt(),
                guide.updatedAt());
    }

    public VoteResponse toVoteResponse(boolean voted, int upvotesCount, String message) {
        return new VoteResponse(voted, upvotesCount, message);
    }

    public MessageResponse toMessageResponse(String message) {
        return new MessageResponse(message);
    }
}