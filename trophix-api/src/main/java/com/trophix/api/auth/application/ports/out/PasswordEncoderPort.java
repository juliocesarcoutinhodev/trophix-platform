package com.trophix.api.auth.application.ports.out;

public interface PasswordEncoderPort {

    /** Encodes a raw plain-text password using BCrypt. */
    String encode(String rawPassword);

    /** Verifies a raw password against a stored BCrypt hash. */
    boolean matches(String rawPassword, String encodedPassword);
}
