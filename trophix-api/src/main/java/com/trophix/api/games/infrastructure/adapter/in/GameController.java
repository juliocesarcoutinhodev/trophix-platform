package com.trophix.api.games.infrastructure.adapter.in;

import com.trophix.api.games.application.ports.in.GetGameDetailUseCase;
import com.trophix.api.games.infrastructure.adapter.in.dto.GameDetailResponse;
import com.trophix.api.games.infrastructure.adapter.in.mapper.GameWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GetGameDetailUseCase getGameDetailUseCase;
    private final GameWebMapper gameWebMapper;

    @GetMapping("/{gameId}/detail")
    public ResponseEntity<GameDetailResponse> getGameDetail(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID gameId) {
        GameDetailResponse detail = gameWebMapper.toGameDetailResponse(
                getGameDetailUseCase.getGameDetail(parseUserId(userId), gameId));
        return ResponseEntity.ok(detail);
    }

    /**
     * Anonymous requests leave the principal as "anonymousUser" (or null);
     * only a valid UUID is treated as a logged-in user.
     */
    private UUID parseUserId(String userId) {
        if (userId == null || userId.isBlank() || "anonymousUser".equals(userId)) {
            return null;
        }
        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
