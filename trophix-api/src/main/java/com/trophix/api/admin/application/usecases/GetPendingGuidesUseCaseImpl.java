package com.trophix.api.admin.application.usecases;

import com.trophix.api.admin.application.ports.in.GetPendingGuidesUseCase;
import com.trophix.api.guides.application.ports.out.GuideRepositoryPort;
import com.trophix.api.guides.application.service.GuideEnricher;
import com.trophix.api.guides.model.GuideListItem;
import com.trophix.api.guides.model.GuideStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetPendingGuidesUseCaseImpl implements GetPendingGuidesUseCase {

    private final GuideRepositoryPort guideRepository;
    private final GuideEnricher guideEnricher;

    @Override
    @Transactional(readOnly = true)
    public Page<GuideListItem> getPendingGuides(Pageable pageable) {
        Page<com.trophix.api.guides.model.Guide> guides =
                guideRepository.findByStatus(GuideStatus.PENDING, pageable);
        var enriched = guideEnricher.enrichAll(guides.getContent(), null);
        return new PageImpl<>(enriched, pageable, guides.getTotalElements());
    }
}
