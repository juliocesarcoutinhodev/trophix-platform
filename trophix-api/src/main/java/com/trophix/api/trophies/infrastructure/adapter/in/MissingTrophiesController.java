package com.trophix.api.trophies.infrastructure.adapter.in;

import com.trophix.api.trophies.application.ports.in.GetMissingTrophiesUseCase;
import com.trophix.api.trophies.infrastructure.adapter.in.dto.TrophyResponse;
import com.trophix.api.trophies.infrastructure.adapter.in.mapper.TrophyWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Trophies the logged-in user has not earned yet for a given game.
 * Lives in the trophies module (the route is under /api/users/me for the
 * frontend), so it keeps the module dependency graph acyclic.
 */
@RestController
@RequestMapping("/api/users/me/games")
@RequiredArgsConstructor
public class MissingTrophiesController {

    private final GetMissingTrophiesUseCase getMissingTrophiesUseCase;
    private final TrophyWebMapper trophyWebMapper;

    @GetMapping("/{gameId}/missing-trophies")
    public ResponseEntity<List<TrophyResponse>> getMissingTrophies(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID gameId) {
        List<TrophyResponse> missing = getMissingTrophiesUseCase
                .getMissingTrophies(UUID.fromString(userId), gameId).stream()
                .map(trophyWebMapper::toTrophyResponse)
                .toList();
        return ResponseEntity.ok(missing);
    }
}
