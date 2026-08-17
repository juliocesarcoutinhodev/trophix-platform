package com.trophix.api.games.model;

/**
 * Result of a find-or-create: the persisted game and whether it was created
 * in this operation (vs. already existing). Pure Java.
 */
public record GameSaveResult(Game game, boolean created) {
}
