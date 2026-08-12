package com.trophix.api.users.model;

/**
 * Public PSN profile data fetched from the sidecar.
 */
public record PsnProfile(String psnId, String aboutMe, String avatarUrl) {
}