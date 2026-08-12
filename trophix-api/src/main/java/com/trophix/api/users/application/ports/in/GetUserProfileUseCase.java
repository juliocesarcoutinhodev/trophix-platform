package com.trophix.api.users.application.ports.in;

import com.trophix.api.users.model.User;

public interface GetUserProfileUseCase {

    /**
     * Finds a public user profile by its PSN online id.
     *
     * @throws com.trophix.api.shared.exception.ResourceNotFoundException
     *         when the user does not exist
     */
    User getProfile(String username);
}