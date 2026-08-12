package com.trophix.api.users.infrastructure.adapter.in.mapper;

import com.trophix.api.users.infrastructure.adapter.in.dto.AccountLinkTokenResponse;
import com.trophix.api.users.infrastructure.adapter.in.dto.AccountLinkValidationResponse;
import com.trophix.api.users.model.AccountLinkValidation;
import org.springframework.stereotype.Component;

/**
 * Converts inbound web DTOs into application responses, keeping the
 * controller free of manual instantiation and mapping logic.
 */
@Component
public class UserWebMapper {

    public AccountLinkTokenResponse toTokenResponse(String token) {
        return new AccountLinkTokenResponse(token);
    }

    public AccountLinkValidationResponse toValidationResponse(AccountLinkValidation validation) {
        return new AccountLinkValidationResponse(
                validation.userId().toString(),
                validation.psnId(),
                validation.message());
    }
}