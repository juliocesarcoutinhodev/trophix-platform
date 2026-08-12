package com.trophix.api.users.application.ports.in;

public interface RequestAccountLinkUseCase {

    /**
     * Generates a short verification token and stores a 15-minute ticket.
     *
     * @return the generated token, e.g. "TRFX-1234"
     */
    String requestLink(String psnId);
}