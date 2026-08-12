package com.trophix.api.auth.infrastructure.adapter.out;

import com.trophix.api.auth.application.ports.out.TokenGeneratorPort;
import com.trophix.api.auth.application.ports.out.TokenValidatorPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtTokenAdapter implements TokenGeneratorPort, TokenValidatorPort {

    private static final String ROLES_CLAIM = "roles";

    @Value("${trophix.jwt.secret}")
    private String secret;

    @Value("${trophix.jwt.expiration:PT24H}")
    private Duration expiration;

    @Override
    public String generate(String subject, Collection<String> roles) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + expiration.toMillis());

        return Jwts.builder()
                .subject(subject)
                .claim(ROLES_CLAIM, roles)
                .issuedAt(now)
                .expiration(expiresAt)
                .signWith(secretKey())
                .compact();
    }

    @Override
    public TokenClaims validate(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        @SuppressWarnings("unchecked")
        List<String> roles = claims.get(ROLES_CLAIM, List.class);

        return new TokenClaims(claims.getSubject(), roles != null ? roles : List.of());
    }

    private SecretKey secretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
