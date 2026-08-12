package com.trophix.api.guides.infrastructure.adapter.in.dto;

public record VoteResponse(
        boolean voted,
        Integer upvotesCount,
        String message) {
}