package com.trophix.api.trophies.infrastructure.adapter.in;

import com.trophix.api.games.application.ports.out.GameRepositoryPort;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import com.trophix.api.trophies.application.ports.in.SyncGameTrophiesUseCase;
import com.trophix.api.trophies.application.ports.out.TrophyRepositoryPort;
import com.trophix.api.trophies.infrastructure.adapter.in.dto.MessageResponse;
import com.trophix.api.trophies.infrastructure.adapter.in.dto.TrophyResponse;
import com.trophix.api.trophies.infrastructure.adapter.in.mapper.TrophyWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameTrophyController {

    private final SyncGameTrophiesUseCase syncGameTrophiesUseCase;
    private final TrophyRepositoryPort trophyRepository;
    private final GameRepositoryPort gameRepository;
    private final TrophyWebMapper trophyWebMapper;

    @PostMapping("/{gameId}/sync-trophies")
    public ResponseEntity<MessageResponse> syncTrophies(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID gameId) {
        String message = syncGameTrophiesUseCase.sync(UUID.fromString(userId), gameId);
        return ResponseEntity.ok(new MessageResponse(message));
    }

    @GetMapping("/{gameId}/trophies")
    public ResponseEntity<List<TrophyResponse>> getTrophies(@PathVariable UUID gameId) {
        gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Jogo não encontrado"));

        List<TrophyResponse> trophies = trophyRepository.findByGameId(gameId).stream()
                .map(trophyWebMapper::toTrophyResponse)
                .toList();
        return ResponseEntity.ok(trophies);
    }
}