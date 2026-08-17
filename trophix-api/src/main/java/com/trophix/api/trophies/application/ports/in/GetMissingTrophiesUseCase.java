package com.trophix.api.trophies.application.ports.in;

import com.trophix.api.trophies.model.Trophy;

import java.util.List;
import java.util.UUID;

/**
 * Returns the trophies of a game the user has not earned yet.
 */
public interface GetMissingTrophiesUseCase {

    List<Trophy> getMissingTrophies(UUID userId, UUID gameId);
}
