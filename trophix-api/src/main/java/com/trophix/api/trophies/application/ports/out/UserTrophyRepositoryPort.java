package com.trophix.api.trophies.application.ports.out;

import com.trophix.api.trophies.model.UserTrophy;

import java.util.List;

public interface UserTrophyRepositoryPort {

    /**
     * Inserts or updates the user's earned trophies (upsert by user+trophy).
     */
    void saveAll(List<UserTrophy> userTrophies);
}