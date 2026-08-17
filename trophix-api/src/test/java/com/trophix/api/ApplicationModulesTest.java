package com.trophix.api;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Verifies the Spring Modulith application module arrangement.
 *
 * Every module exposes its public API through {@code @NamedInterface}
 * package-info files and there are no cyclic or non-exposed-type
 * dependencies between modules. This test must stay green.
 */
class ApplicationModulesTest {

    @Test
    void verifyModuleStructure() {
        ApplicationModules.of(TrophixApiApplication.class).verify();
    }
}
