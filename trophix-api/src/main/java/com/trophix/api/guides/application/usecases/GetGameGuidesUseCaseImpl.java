package com.trophix.api.guides.application.usecases;

import com.trophix.api.games.application.ports.out.GameRepositoryPort;
import com.trophix.api.games.model.Game;
import com.trophix.api.guides.application.ports.in.GetGameGuidesUseCase;
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
public class GetGameGuidesUseCaseImpl implements GetGameGuidesUseCase {

    private final GameRepositoryPort gameRepository;
    private final GuideRepositoryPort guideRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Guide> getApprovedGuides(String npCommunicationId) {
        UUID gameId = gameRepository.findByNpCommunicationId(npCommunicationId)
                .map(Game::id)
                .orElseThrow(() -> new ResourceNotFoundException("Jogo não encontrado"));

        return guideRepository.findByGameIdAndStatusOrderByUpvotesCountDesc(gameId, GuideStatus.APPROVED);
    }
}