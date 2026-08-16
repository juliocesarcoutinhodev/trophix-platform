package com.trophix.api.auth.application.ports.out;

import java.util.UUID;

/**
 * Outbound port for transactional e-mail delivery (SMTP via Mailpit in dev).
 */
public interface EmailSenderPort {

    /**
     * Sends the password reset e-mail to the given recipient.
     * Implementations may run asynchronously.
     *
     * @param to        recipient e-mail
     * @param username  display name for the greeting
     * @param resetUrl  full reset link (front-end route with the token)
     */
    void sendPasswordReset(String to, String username, String resetUrl);

    /**
     * Notifies a topic author that someone replied to their forum topic.
     * Implementations may run asynchronously.
     *
     * @param to                  topic author e-mail
     * @param topicAuthorUsername topic author display name
     * @param replyAuthorUsername author of the reply
     * @param topicTitle          title of the replied topic
     * @param topicId             id used to build the "view topic" link
     */
    void sendReplyNotification(String to, String topicAuthorUsername,
                               String replyAuthorUsername, String topicTitle, UUID topicId);
}
