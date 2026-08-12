package com.trophix.api.users.infrastructure.adapter.in.dto;

public record AccountLinkValidationResponse(
        String userId,
        String psnId,
        String message) {
}