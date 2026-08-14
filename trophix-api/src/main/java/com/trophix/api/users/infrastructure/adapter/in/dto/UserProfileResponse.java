package com.trophix.api.users.infrastructure.adapter.in.dto;

import java.util.Set;

public record UserProfileResponse(
        String username,
        String avatarUrl,
        Integer psnLevel,
        Integer levelProgress,
        Integer totalPlatinum,
        Integer totalGold,
        Integer totalSilver,
        Integer totalBronze,
        Set<String> roles) {
}