package com.trophix.api.shared.infrastructure.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Applies per-client-IP rate limits to API routes before authentication.
 * Route groups: {@code auth} (login/register/link — strict, anti brute-force),
 * {@code public-read} (public profile/games/guides/trophies — moderate) and
 * {@code default} (remaining authenticated routes — generous).
 * Rejects excess requests with HTTP 429 (PT-BR) and {@code Retry-After}.
 * In-memory per instance; move to Redis or the gateway for multi-instance.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    public static final String GROUP_AUTH = "auth";
    public static final String GROUP_PUBLIC_READ = "public-read";
    public static final String GROUP_DEFAULT = "default";

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private static final List<String> AUTH_POST_PATTERNS = List.of(
            "/api/auth/login",
            "/api/auth/register-completion",
            "/api/users/link-request",
            "/api/users/link-validate");

    private static final List<String> PUBLIC_GET_PATTERNS = List.of(
            "/api/users/*/profile",
            "/api/users/*/games",
            "/api/trophies/*/guides",
            "/api/games/np/*/guides",
            "/api/games/*/trophies",
            "/api/games/*/authors/*/trophy-guides",
            "/api/guides/**");

    private final RateLimitProperties properties;
    private final Map<String, RateLimiter> limiters;

    public RateLimitFilter(RateLimitProperties properties) {
        this.properties = properties;
        this.limiters = new HashMap<>();
        properties.limits().forEach((name, limit) ->
                limiters.put(name, new RateLimiter(name, limit.capacity(), limit.refillPerMinute())));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (!properties.enabled() || request.getMethod().equals("OPTIONS")) {
            chain.doFilter(request, response);
            return;
        }

        String group = resolveGroup(request);
        if (group == null) {
            chain.doFilter(request, response);
            return;
        }

        RateLimiter limiter = limiters.get(group);
        String key = clientIp(request) + "|" + group;
        if (limiter == null || limiter.tryAcquire(key)) {
            chain.doFilter(request, response);
            return;
        }

        response.setStatus(429);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Retry-After", "1");
        response.getWriter().write(
                "{\"status\":429,\"message\":\"Muitas requisições. Tente novamente em instantes.\"}");
    }

    private String resolveGroup(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (!path.startsWith("/api")) {
            return null;
        }
        String method = request.getMethod();
        if (method.equals("POST") && matches(path, AUTH_POST_PATTERNS)) {
            return GROUP_AUTH;
        }
        if (method.equals("GET") && matches(path, PUBLIC_GET_PATTERNS)) {
            return GROUP_PUBLIC_READ;
        }
        return GROUP_DEFAULT;
    }

    private boolean matches(String path, List<String> patterns) {
        return patterns.stream().anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
    }

    private String clientIp(HttpServletRequest request) {
        if (properties.trustForwardedHeader()) {
            String forwardedFor = request.getHeader("X-Forwarded-For");
            if (forwardedFor != null && !forwardedFor.isBlank()) {
                return forwardedFor.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }
}
