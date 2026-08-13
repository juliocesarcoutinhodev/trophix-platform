package com.trophix.api.guides.application.usecases;

import com.trophix.api.guides.application.ports.in.GetLatestGuidesUseCase;
import com.trophix.api.guides.application.ports.out.GuideRepositoryPort;
import com.trophix.api.guides.application.service.GuideEnricher;
import com.trophix.api.guides.model.Guide;
import com.trophix.api.guides.model.GuideListItem;
import com.trophix.api.guides.model.GuideStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetLatestGuidesUseCaseImpl implements GetLatestGuidesUseCase {

    private static final int MAX_LIMIT = 50;

    private final GuideRepositoryPort guideRepository;
    private final GuideEnricher guideEnricher;

    @Override
    @Transactional(readOnly = true)
    public List<GuideListItem> getLatestRoadmaps(int limit, UUID currentUserId) {
        int safeLimit = Math.max(1, Math.min(limit, MAX_LIMIT));
        List<Guide> guides = guideRepository.findLatestRoadmapsByStatus(GuideStatus.APPROVED, safeLimit);
        return guideEnricher.enrichAll(guides, currentUserId);
    }
}
