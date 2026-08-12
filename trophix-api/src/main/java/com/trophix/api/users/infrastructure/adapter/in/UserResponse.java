package com.trophix.api.users.infrastructure.adapter.in;

import com.trophix.api.users.model.User;

import java.util.UUID;

public record UserResponse(UUID id, String username, String email, String avatarUrl) {

    public static UserResponse from(User user) {
        return new UserResponse(user.id(), user.username(), user.email(), user.avatarUrl());
    }
}