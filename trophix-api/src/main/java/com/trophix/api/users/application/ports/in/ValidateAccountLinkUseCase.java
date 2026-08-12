package com.trophix.api.users.application.ports.in;

import com.trophix.api.users.model.AccountLinkValidation;

public interface ValidateAccountLinkUseCase {

    /**
     * Validates the account ownership by checking the PSN "About Me" for
     * the pending verification token.
     */
    AccountLinkValidation validateLink(String psnId);
}