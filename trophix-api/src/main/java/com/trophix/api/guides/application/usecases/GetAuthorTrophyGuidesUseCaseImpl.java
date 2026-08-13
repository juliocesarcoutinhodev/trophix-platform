package com.trophix.api.guides.application.usecases;

import com.trophix.api.games.application.ports.out.GameRepositoryPort;
import com.trophix.api.guides.application.ports.in.GetAuthorTrophyGuidesUseCase;
import com.trophix.api.guides.application.ports.out.GuideRepositoryPort;
import com.trophix.api.guides.model.Guide;
import com.trophix.api.guides.model.GuideStatus;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetAuthorTrophyGuidesUseCaseImpl implements GetAuthorTrophyGuidesUseCase {

    private final GuideRepositoryPort guideRepository;
    private final GameRepositoryPort gameRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Guide> getAuthorTrophyGuides(UUID gameId, UUID authorId) {
        gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Jogo não encontrado"));

        return guideRepository.findTrophyTipsByAuthorAndGame(gameId, authorId, GuideStatus.APPROVED);
    }
}
