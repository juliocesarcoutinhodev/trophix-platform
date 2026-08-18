package com.trophix.api.admin.application.usecases;

import com.trophix.api.admin.application.ports.in.SetGameFeaturedUseCase;
import com.trophix.api.games.application.ports.out.GameRepositoryPort;
import com.trophix.api.games.model.Game;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class SetGameFeaturedUseCaseImpl implements SetGameFeaturedUseCase {

    private final GameRepositoryPort gameRepository;

    @Override
    @Transactional
    public Game execute(SetGameFeaturedCommand command) {
        Game game = gameRepository.findById(command.gameId())
                .orElseThrow(() -> new ResourceNotFoundException("Jogo não encontrado"));

        Game updated = game.withFeatured(command.isFeatured());
        Game saved = gameRepository.save(updated);

        log.info("Destaque do jogo alterado pelo admin: gameId={} isFeatured={}",
                saved.id(), saved.featured());
        return saved;
    }
}
