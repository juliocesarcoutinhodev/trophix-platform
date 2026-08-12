package com.trophix.api.auth.application.ports.out;

import java.util.List;

public interface TokenValidatorPort {

    /**
     * Validates and parses a JWT token.
     *
     * @param token compact JWT string
     * @return parsed claims
     * @throws io.jsonwebtoken.JwtException if the token is invalid or expired
     */
    TokenClaims validate(String token);

    record TokenClaims(String subject, List<String> roles) {}
}
