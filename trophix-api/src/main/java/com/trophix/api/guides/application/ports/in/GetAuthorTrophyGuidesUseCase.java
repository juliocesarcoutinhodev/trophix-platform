package com.trophix.api.guides.application.ports.in;

import com.trophix.api.guides.model.Guide;

import java.util.List;
import java.util.UUID;

public interface GetAuthorTrophyGuidesUseCase {

    /**
     * Returns the APPROVED trophy tips (guides without a game target) written
     * by the author for the trophies of the given game.
     */
    List<Guide> getAuthorTrophyGuides(UUID gameId, UUID authorId);
}
