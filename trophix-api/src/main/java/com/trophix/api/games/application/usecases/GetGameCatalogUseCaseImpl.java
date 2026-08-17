package com.trophix.api.games.application.usecases;

import com.trophix.api.games.application.ports.in.GetGameCatalogUseCase;
import com.trophix.api.games.application.ports.out.GameRepositoryPort;
import com.trophix.api.games.model.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetGameCatalogUseCaseImpl implements GetGameCatalogUseCase {

    private final GameRepositoryPort gameRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Game> getCatalog(String search, Pageable pageable) {
        return gameRepository.findCatalog(search, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Game> getTrending(int limit) {
        return gameRepository.findTrending(limit);
    }
}
