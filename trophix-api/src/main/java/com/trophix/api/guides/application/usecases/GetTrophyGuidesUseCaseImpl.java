package com.trophix.api.guides.application.usecases;

import com.trophix.api.guides.application.ports.in.GetTrophyGuidesUseCase;
import com.trophix.api.guides.application.ports.out.GuideRepositoryPort;
import com.trophix.api.guides.model.Guide;
import com.trophix.api.guides.model.GuideStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetTrophyGuidesUseCaseImpl implements GetTrophyGuidesUseCase {

    private final GuideRepositoryPort guideRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Guide> getApprovedGuides(UUID trophyId) {
        return guideRepository.findByTrophyIdAndStatusOrderByUpvotesCountDesc(trophyId, GuideStatus.APPROVED);
    }
}