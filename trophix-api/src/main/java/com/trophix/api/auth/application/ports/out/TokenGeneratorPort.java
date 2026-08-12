package com.trophix.api.auth.application.ports.out;

import java.util.Collection;

public interface TokenGeneratorPort {

    /**
     * Generates a signed JWT for the given subject and roles.
     *
     * @param subject the token subject (user email)
     * @param roles   the granted roles (e.g. ["ROLE_USER"])
     * @return signed compact JWT string
     */
    String generate(String subject, Collection<String> roles);
}
