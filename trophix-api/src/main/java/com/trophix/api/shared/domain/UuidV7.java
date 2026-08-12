package com.trophix.api.shared.domain;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * RFC 9562 UUIDv7 generator (time-ordered, sortable primary keys).
 * Pure Java, no framework or database dependency required.
 */
public final class UuidV7 {

    private static final long TIMESTAMP_MASK = 0xFFFFFFFFFFFFL;
    private static final long VERSION_BITS = 0x7000L;
    private static final long VARIANT_BITS = 0x8000000000000000L;
    private static final long RANDOM_12_BITS_MASK = 0x0FFFL;
    private static final long RANDOM_62_BITS_MASK = 0x3FFFFFFFFFFFFFFFL;

    private UuidV7() {
    }

    public static UUID generate() {
        long millis = System.currentTimeMillis() & TIMESTAMP_MASK;
        long random12Bits = ThreadLocalRandom.current().nextLong() & RANDOM_12_BITS_MASK;
        long random62Bits = ThreadLocalRandom.current().nextLong() & RANDOM_62_BITS_MASK;

        long mostSignificantBits = (millis << 16) | VERSION_BITS | random12Bits;
        long leastSignificantBits = VARIANT_BITS | random62Bits;

        return new UUID(mostSignificantBits, leastSignificantBits);
    }
}