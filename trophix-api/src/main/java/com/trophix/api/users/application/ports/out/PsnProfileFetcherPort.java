package com.trophix.api.users.application.ports.out;

import com.trophix.api.users.model.PsnProfile;

public interface PsnProfileFetcherPort {

    /**
     * Fetches the public PSN profile for the given online id.
     *
     * @throws com.trophix.api.shared.exception.ResourceNotFoundException when the
     *         profile does not exist or is private (sidecar HTTP 404)
     */
    PsnProfile fetchByPsnId(String psnId);
}