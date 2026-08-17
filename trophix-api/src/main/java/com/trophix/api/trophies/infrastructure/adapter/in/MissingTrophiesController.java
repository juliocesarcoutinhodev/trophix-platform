package com.trophix.api.trophies.infrastructure.adapter.in;

import com.trophix.api.trophies.application.ports.in.GetGlobalMissingTrophiesUseCase;
import com.trophix.api.trophies.application.ports.in.GetMissingTrophiesUseCase;
import com.trophix.api.trophies.infrastructure.adapter.in.dto.MissingTrophyDTO;
import com.trophix.api.trophies.infrastructure.adapter.in.dto.TrophyResponse;
import com.trophix.api.trophies.infrastructure.adapter.in.mapper.TrophyWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Trophies the logged-in user has not earned yet (per game or across all of
 * their games). Lives in the trophies module (the routes are under
 * /api/users/me for the frontend), so it keeps the module dependency graph
 * acyclic.
 */
@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class MissingTrophiesController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final GetMissingTrophiesUseCase getMissingTrophiesUseCase;
    private final GetGlobalMissingTrophiesUseCase getGlobalMissingTrophiesUseCase;
    private final TrophyWebMapper trophyWebMapper;

    @GetMapping("/games/{gameId}/missing-trophies")
    public ResponseEntity<List<TrophyResponse>> getMissingTrophies(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID gameId) {
        List<TrophyResponse> missing = getMissingTrophiesUseCase
                .getMissingTrophies(UUID.fromString(userId), gameId).stream()
                .map(trophyWebMapper::toTrophyResponse)
                .toList();
        return ResponseEntity.ok(missing);
    }

    @GetMapping("/trophies/missing")
    public ResponseEntity<Page<MissingTrophyDTO>> getGlobalMissingTrophies(
            @AuthenticationPrincipal String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<MissingTrophyDTO> missing = getGlobalMissingTrophiesUseCase
                .getGlobalMissingTrophies(
                        UUID.fromString(userId),
                        PageRequest.of(Math.max(page, 0), Math.min(size, 100)))
                .map(trophyWebMapper::toMissingTrophyDTO);
        return ResponseEntity.ok(missing);
    }
}
