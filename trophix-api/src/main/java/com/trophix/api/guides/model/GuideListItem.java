package com.trophix.api.guides.model;

/**
 * Read model for guide listings/details: the guide enriched with the target
 * game name/image, the author's name/avatar and whether the current user
 * voted. Pure Java.
 */
public record GuideListItem(
        Guide guide,
        String gameName,
        String imageUrl,
        String authorName,
        String authorAvatarUrl,
        boolean currentUserVoted) {
}
