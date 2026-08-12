package com.trophix.api.shared.infrastructure.security;

import com.trophix.api.auth.application.ports.out.TokenValidatorPort;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String COOKIE_NAME = "trophix_jwt";

    private final TokenValidatorPort tokenValidator;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        extractJwtFromCookies(request)
                .ifPresent(token -> authenticate(token, request));

        filterChain.doFilter(request, response);
    }

    private java.util.Optional<String> extractJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return java.util.Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(c -> COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .filter(value -> !value.isBlank())
                .findFirst();
    }

    private void authenticate(String token, HttpServletRequest request) {
        try {
            TokenValidatorPort.TokenClaims claims = tokenValidator.validate(token);

            List<SimpleGrantedAuthority> authorities = claims.roles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            var authentication = new UsernamePasswordAuthenticationToken(
                    claims.subject(), null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("JWT autenticado para subject={} uri={}", claims.subject(), request.getRequestURI());

        } catch (JwtException ex) {
            log.debug("JWT inválido ou expirado: {}", ex.getMessage());
            // Não seta autenticação — Spring Security rejeitará a requisição
        }
    }
}
