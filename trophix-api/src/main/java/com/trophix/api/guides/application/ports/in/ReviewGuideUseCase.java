package com.trophix.api.guides.application.ports.in;

import java.util.UUID;

public interface ReviewGuideUseCase {

    String review(UUID adminId, UUID guideId, ReviewAction action);

    enum ReviewAction {
        APPROVE,
        REJECT
    }
}