package com.trophix.api.forums.model;

import java.util.UUID;

/**
 * Domain event published strictly locally when a reply is created, so
 * interested listeners (e.g. the e-mail notification) react asynchronously
 * without the forums module knowing them. Carries only ids to keep the
 * Modulith event journal entry small. Pure Java.
 */
public record ReplyCreatedEvent(
        UUID topicId,
        UUID replyAuthorId) {
}
