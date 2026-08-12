package com.trophix.api.trophies.infrastructure.adapter.in;

import com.trophix.api.trophies.application.ports.in.SyncGameTrophiesUseCase;
import com.trophix.api.trophies.infrastructure.adapter.in.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameTrophyController {

    private final SyncGameTrophiesUseCase syncGameTrophiesUseCase;

    @PostMapping("/{gameId}/sync-trophies")
    public ResponseEntity<MessageResponse> syncTrophies(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID gameId) {
        String message = syncGameTrophiesUseCase.sync(UUID.fromString(userId), gameId);
        return ResponseEntity.ok(new MessageResponse(message));
    }
}