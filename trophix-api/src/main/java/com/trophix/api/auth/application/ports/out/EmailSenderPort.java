package com.trophix.api.auth.application.ports.out;

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
}
