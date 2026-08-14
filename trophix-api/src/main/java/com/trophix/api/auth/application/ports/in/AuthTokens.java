package com.trophix.api.auth.application.ports.in;

/**
 * Result of a successful session issuance/rotation. The access token is a short
 * lived JWT; the refresh token is the raw opaque value that must be placed in
 * the HttpOnly cookie (never persisted server-side).
 */
public record AuthTokens(String accessToken, String refreshToken) {
}
