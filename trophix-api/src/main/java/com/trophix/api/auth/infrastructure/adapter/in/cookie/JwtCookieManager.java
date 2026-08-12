package com.trophix.api.auth.infrastructure.adapter.in.cookie;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Encapsulates the HttpOnly JWT cookie concerns: name, security flags and
 * expiry, including the logout (expiration) semantics.
 */
@Component
public class JwtCookieManager {

    public static final String COOKIE_NAME = "trophix_jwt";
    private static final Duration JWT_COOKIE_MAX_AGE = Duration.ofHours(24);

    private final boolean cookieSecure;

    public JwtCookieManager(@Value("${trophix.jwt.cookie-secure:false}") boolean cookieSecure) {
        this.cookieSecure = cookieSecure;
    }

    public ResponseCookie generateJwtCookie(String token) {
        return buildCookie(token, JWT_COOKIE_MAX_AGE);
    }

    public ResponseCookie generateLogoutCookie() {
        return buildCookie("", Duration.ZERO);
    }

    private ResponseCookie buildCookie(String value, Duration maxAge) {
        return ResponseCookie.from(COOKIE_NAME, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}