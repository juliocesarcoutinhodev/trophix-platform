package com.trophix.api.auth.infrastructure.adapter.in.dto;

import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String psnId,
        String email,
        String avatarUrl,
        Set<String> roles) {
}