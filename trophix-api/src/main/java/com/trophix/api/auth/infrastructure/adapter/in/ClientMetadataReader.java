package com.trophix.api.auth.infrastructure.adapter.in;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * Extracts the client request metadata (IP, User-Agent) used to audit the
 * creation of refresh tokens. Keeps the controller free of raw servlet reads.
 */
@Component
public class ClientMetadataReader {

    public ClientMetadata read(HttpServletRequest request) {
        return new ClientMetadata(request.getRemoteAddr(), request.getHeader("User-Agent"));
    }

    public record ClientMetadata(String ipAddress, String userAgent) {}
}
