package com.trophix.api.trophies.application.usecases;

import com.trophix.api.trophies.application.ports.in.GetGlobalMissingTrophiesUseCase;
import com.trophix.api.trophies.application.ports.out.TrophyRepositoryPort;
import com.trophix.api.trophies.model.MissingTrophy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetGlobalMissingTrophiesUseCaseImpl implements GetGlobalMissingTrophiesUseCase {

    private final TrophyRepositoryPort trophyRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<MissingTrophy> getGlobalMissingTrophies(UUID userId, Pageable pageable) {
        return trophyRepository.findMissingForUser(userId, pageable);
    }
}
