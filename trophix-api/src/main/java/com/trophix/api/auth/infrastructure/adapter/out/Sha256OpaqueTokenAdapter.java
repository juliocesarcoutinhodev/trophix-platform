package com.trophix.api.auth.infrastructure.adapter.out;

import com.trophix.api.auth.application.ports.out.OpaqueTokenPort;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Generates opaque refresh tokens via CSPRNG (256 bits, base64url) and hashes
 * them with SHA-256 for server-side storage. The raw token is never persisted.
 */
@Component
public class Sha256OpaqueTokenAdapter implements OpaqueTokenPort {

    private static final int TOKEN_BYTES = 32; // 256 bits of entropy
    private static final String SHA_256 = "SHA-256";

    private final SecureRandom secureRandom;

    public Sha256OpaqueTokenAdapter() {
        this.secureRandom = new SecureRandom();
    }

    @Override
    public String generate() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Override
    public String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance(SHA_256)
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível no runtime", e);
        }
    }
}
