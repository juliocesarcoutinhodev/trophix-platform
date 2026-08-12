package com.trophix.api.guides.application.ports.in;

import com.trophix.api.guides.model.Guide;

import java.util.List;

public interface GetGameGuidesUseCase {

    /**
     * Returns the APPROVED game roadmaps (guides) for a game, identified
     * by its PSN npCommunicationId.
     */
    List<Guide> getApprovedGuides(String npCommunicationId);
}