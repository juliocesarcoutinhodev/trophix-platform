package com.trophix.api.trophies.application.ports.in;

import com.trophix.api.trophies.model.MissingTrophy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Returns the trophies the user has not earned yet across all of their games.
 */
public interface GetGlobalMissingTrophiesUseCase {

    Page<MissingTrophy> getGlobalMissingTrophies(UUID userId, Pageable pageable);
}
