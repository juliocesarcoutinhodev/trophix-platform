package com.trophix.api;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.core.Violation;
import org.springframework.modulith.core.Violations;

import java.util.Set;

/**
 * Verifies the Spring Modulith application module arrangement.
 *
 * The module APIs are exposed via {@code @NamedInterface} (see the
 * package-info files) and there are no cross-module accesses to
 * non-exposed types left. The only remaining legacy violation is the
 * use-case level cycle games↔trophies (and via users), which is being
 * resolved in a dedicated phase. Remove this entry once that cycle is
 * broken.
 */
class ApplicationModulesTest {

    private static final Set<String> KNOWN_VIOLATIONS = Set.of(
            "Cycle detected: Slice games ->"
    );

    @Test
    void verifyModuleStructure() {
        ApplicationModules modules = ApplicationModules.of(TrophixApiApplication.class);
        Violations remaining = modules.detectViolations()
                .filter(v -> !KNOWN_VIOLATIONS.contains(firstLine(v.getMessage())));
        remaining.throwIfPresent();
    }

    private static String firstLine(String message) {
        int newline = message.indexOf('\n');
        return (newline >= 0 ? message.substring(0, newline) : message).strip();
    }
}
