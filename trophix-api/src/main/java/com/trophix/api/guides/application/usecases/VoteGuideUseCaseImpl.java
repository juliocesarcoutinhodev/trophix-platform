package com.trophix.api.guides.application.usecases;

import com.trophix.api.guides.application.ports.in.VoteGuideUseCase;
import com.trophix.api.guides.application.ports.out.GuideRepositoryPort;
import com.trophix.api.guides.application.ports.out.GuideVoteRepositoryPort;
import com.trophix.api.guides.model.GuideVote;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class VoteGuideUseCaseImpl implements VoteGuideUseCase {

    private final GuideRepositoryPort guideRepository;
    private final GuideVoteRepositoryPort guideVoteRepository;

    @Override
    @Transactional
    public VoteResult vote(UUID guideId, UUID userId) {
        if (!guideRepository.existsById(guideId)) {
            throw new ResourceNotFoundException("Guia não encontrado");
        }

        boolean alreadyVoted = guideVoteRepository.existsByGuideIdAndUserId(guideId, userId);

        if (alreadyVoted) {
            guideVoteRepository.deleteByGuideIdAndUserId(guideId, userId);
            guideRepository.decrementUpvotesCount(guideId);
        } else {
            guideVoteRepository.save(GuideVote.create(guideId, userId));
            guideRepository.incrementUpvotesCount(guideId);
        }

        // Re-read after the atomic counter update; the entity was not loaded
        // into the persistence context, so this fetch is fresh.
        int upvotesCount = guideRepository.findById(guideId)
                .orElseThrow(() -> new ResourceNotFoundException("Guia não encontrado"))
                .upvotesCount();

        log.info("Voto {} para guia {} pelo usuário {}", alreadyVoted ? "removido" : "registrado", guideId, userId);
        return new VoteResult(
                !alreadyVoted,
                upvotesCount,
                alreadyVoted ? "Voto removido." : "Voto registrado.");
    }
}