package com.trophix.api.forums.model;

/**
 * Topic read model enriched with the author's name and avatar. Pure Java.
 */
public record TopicListItem(
        Topic topic,
        String authorName,
        String authorAvatarUrl) {
}
