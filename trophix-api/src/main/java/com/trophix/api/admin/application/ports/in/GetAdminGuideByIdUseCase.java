package com.trophix.api.admin.application.ports.in;

import com.trophix.api.guides.infrastructure.adapter.in.dto.GuideResponse;
import com.trophix.api.guides.model.Guide;
import java.util.UUID;

public interface GetAdminGuideByIdUseCase {
    Guide getAdminGuide(UUID guideId);
}
