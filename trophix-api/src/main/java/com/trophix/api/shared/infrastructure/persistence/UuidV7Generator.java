package com.trophix.api.shared.infrastructure.persistence;

import com.trophix.api.shared.domain.UuidV7;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.util.UUID;

/**
 * Hibernate identifier generator producing RFC 9562 UUIDv7 primary keys.
 */
public class UuidV7Generator implements IdentifierGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor session, Object ownerObject) {
        return UuidV7.generate();
    }

    @Override
    public boolean allowAssignedIdentifiers() {
        return true;
    }
}