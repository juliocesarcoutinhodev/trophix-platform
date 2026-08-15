package com.trophix.api.admin.application.ports.in;

import java.util.UUID;

/**
 * Permanently removes a guide from the database (admin).
 */
public interface DeleteGuideUseCase {

    void delete(UUID guideId);
}
