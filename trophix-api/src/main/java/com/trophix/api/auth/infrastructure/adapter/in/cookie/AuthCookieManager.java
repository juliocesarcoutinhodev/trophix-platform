package com.trophix.api.auth.infrastructure.adapter.in.cookie;

import com.trophix.api.auth.application.ports.in.AuthTokens;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

/**
 * Encapsulates the session cookies: the short-lived JWT access token
 * ({@code trophix_jwt}) and the opaque refresh token ({@code trophix_refresh}).
 * Both are HttpOnly, SameSite=Strict and, when enabled, Secure. The refresh
 * cookie is scoped to {@code /api/auth} so it only travels to the auth routes.
 */
@Component
public class AuthCookieManager {

    public static final String COOKIE_NAME = "trophix_jwt";
    public static final String REFRESH_COOKIE_NAME = "trophix_refresh";
    private static final String REFRESH_PATH = "/api/auth";

    private final boolean cookieSecure;
    private final Duration accessTokenMaxAge;
    private final Duration refreshTokenMaxAge;

    public AuthCookieManager(
            @Value("${trophix.jwt.cookie-secure:false}") boolean cookieSecure,
            @Value("${trophix.jwt.expiration:PT1H}") Duration accessTokenMaxAge,
            @Value("${trophix.refresh-token.expiration:PT720H}") Duration refreshTokenMaxAge) {
        this.cookieSecure = cookieSecure;
        this.accessTokenMaxAge = accessTokenMaxAge;
        this.refreshTokenMaxAge = refreshTokenMaxAge;
    }

    public ResponseCookie generateAccessCookie(String token) {
        return buildCookie(COOKIE_NAME, token, "/", accessTokenMaxAge);
    }

    public ResponseCookie generateRefreshCookie(String token) {
        return buildCookie(REFRESH_COOKIE_NAME, token, REFRESH_PATH, refreshTokenMaxAge);
    }

    public ResponseCookie generateLogoutCookie() {
        return buildCookie(COOKIE_NAME, "", "/", Duration.ZERO);
    }

    public ResponseCookie generateRefreshLogoutCookie() {
        return buildCookie(REFRESH_COOKIE_NAME, "", REFRESH_PATH, Duration.ZERO);
    }

    /** Headers carrying both session cookies for an issued token pair. */
    public HttpHeaders sessionHeaders(AuthTokens tokens) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, generateAccessCookie(tokens.accessToken()).toString());
        headers.add(HttpHeaders.SET_COOKIE, generateRefreshCookie(tokens.refreshToken()).toString());
        return headers;
    }

    /** Headers clearing both session cookies (logout). */
    public HttpHeaders logoutHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, generateLogoutCookie().toString());
        headers.add(HttpHeaders.SET_COOKIE, generateRefreshLogoutCookie().toString());
        return headers;
    }

    /** Reads the refresh token from the request cookies, if present. */
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(c -> REFRESH_COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .filter(value -> !value.isBlank())
                .findFirst();
    }

    private ResponseCookie buildCookie(String name, String value, String path, Duration maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path(path)
                .maxAge(maxAge)
                .build();
    }
}
