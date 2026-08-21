package com.trophix.api.admin.application.usecases;

import com.trophix.api.admin.application.ports.in.SyncGameCatalogUseCase;
import com.trophix.api.games.application.ports.out.GameRepositoryPort;
import com.trophix.api.shared.application.ports.out.SyncJobPublisher;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class SyncGameCatalogUseCaseImpl implements SyncGameCatalogUseCase {

    private final GameRepositoryPort gameRepository;
    private final SyncJobPublisher syncJobPublisher;

    @Override
    public void syncCatalog(UUID gameId) {
        gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Jogo não encontrado"));

        syncJobPublisher.publishTrophyCatalogSync(gameId);
        log.info("Sync de catalogo encaminhado para a fila: gameId={}", gameId);
    }
}
