package com.trophix.api.auth.application.ports.in;

import com.trophix.api.users.model.User;

public interface CompleteRegistrationUseCase {

    /**
     * Completes registration for a PSN-linked user by adding email, password and ROLE_USER.
     *
     * @return the newly persisted user
     */
    User completeRegistration(RegistrationCommand command);

    record RegistrationCommand(String psnId, String email, String plainPassword) {}
}
