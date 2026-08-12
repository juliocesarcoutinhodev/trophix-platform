package com.trophix.api.users.infrastructure.adapter.in;

import com.trophix.api.users.application.ports.in.RequestAccountLinkUseCase;
import com.trophix.api.users.application.ports.in.ValidateAccountLinkUseCase;
import com.trophix.api.users.infrastructure.adapter.in.dto.AccountLinkRequest;
import com.trophix.api.users.infrastructure.adapter.in.dto.AccountLinkTokenResponse;
import com.trophix.api.users.infrastructure.adapter.in.dto.AccountLinkValidationResponse;
import com.trophix.api.users.infrastructure.adapter.in.mapper.UserWebMapper;
import com.trophix.api.users.model.AccountLinkValidation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRegistrationController {

    private final RequestAccountLinkUseCase requestAccountLinkUseCase;
    private final ValidateAccountLinkUseCase validateAccountLinkUseCase;
    private final UserWebMapper userWebMapper;

    @PostMapping("/link-request")
    public ResponseEntity<AccountLinkTokenResponse> requestLink(
            @Valid @RequestBody AccountLinkRequest request) {
        String token = requestAccountLinkUseCase.requestLink(request.psnId());
        return ResponseEntity.ok(userWebMapper.toTokenResponse(token));
    }

    @PostMapping("/link-validate")
    public ResponseEntity<AccountLinkValidationResponse> validateLink(
            @Valid @RequestBody AccountLinkRequest request) {
        AccountLinkValidation validation = validateAccountLinkUseCase.validateLink(request.psnId());
        return ResponseEntity.ok(userWebMapper.toValidationResponse(validation));
    }
}