package com.trophix.api.forums.model;

/**
 * Reply read model enriched with the author's name and avatar. Pure Java.
 */
public record ReplyListItem(
        Reply reply,
        String authorName,
        String authorAvatarUrl) {
}
