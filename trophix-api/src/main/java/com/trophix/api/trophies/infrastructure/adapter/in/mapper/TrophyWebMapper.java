package com.trophix.api.trophies.infrastructure.adapter.in.mapper;

import com.trophix.api.trophies.infrastructure.adapter.in.dto.TrophyResponse;
import com.trophix.api.trophies.infrastructure.adapter.in.dto.TrophyStatusResponse;
import com.trophix.api.trophies.model.Trophy;
import com.trophix.api.trophies.model.TrophyWithStatus;
import org.springframework.stereotype.Component;

@Component
public class TrophyWebMapper {

    public TrophyResponse toTrophyResponse(Trophy trophy) {
        return new TrophyResponse(
                trophy.id(),
                trophy.psnTrophyId(),
                trophy.name(),
                trophy.description(),
                trophy.type(),
                trophy.iconUrl());
    }

    public TrophyStatusResponse toTrophyStatusResponse(TrophyWithStatus trophyWithStatus) {
        Trophy trophy = trophyWithStatus.trophy();
        return new TrophyStatusResponse(
                trophy.id(),
                trophy.psnTrophyId(),
                trophy.name(),
                trophy.description(),
                trophy.type(),
                trophy.iconUrl(),
                trophyWithStatus.earned(),
                trophyWithStatus.earnedAt());
    }
}