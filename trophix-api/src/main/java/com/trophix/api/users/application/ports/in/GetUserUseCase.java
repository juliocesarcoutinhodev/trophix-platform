package com.trophix.api.users.application.ports.in;

import com.trophix.api.users.model.User;

import java.util.UUID;

public interface GetUserUseCase {

    User getById(UUID userId);
}