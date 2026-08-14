package com.trophix.api.admin.application.ports.in;

import com.trophix.api.guides.model.Guide;

import java.util.UUID;

/**
 * Edits the content fields of a guide (admin). Status and metadata are kept.
 */
public interface UpdateGuideUseCase {

    Guide update(UpdateGuideCommand command);

    record UpdateGuideCommand(UUID guideId, String title, String description,
                              String content, String videoUrl) {
    }
}
