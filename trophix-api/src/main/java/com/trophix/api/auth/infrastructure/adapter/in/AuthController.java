package com.trophix.api.auth.infrastructure.adapter.in;

import com.trophix.api.auth.application.ports.in.AuthTokens;
import com.trophix.api.auth.application.ports.in.CompleteRegistrationUseCase;
import com.trophix.api.auth.application.ports.in.LoginUseCase;
import com.trophix.api.auth.application.ports.in.LogoutUseCase;
import com.trophix.api.auth.application.ports.in.RefreshSessionUseCase;
import com.trophix.api.auth.infrastructure.adapter.in.cookie.AuthCookieManager;
import com.trophix.api.auth.infrastructure.adapter.in.dto.LoginRequest;
import com.trophix.api.auth.infrastructure.adapter.in.dto.RegistrationRequest;
import com.trophix.api.auth.infrastructure.adapter.in.dto.UserResponse;
import com.trophix.api.auth.infrastructure.adapter.in.mapper.AuthWebMapper;
import com.trophix.api.shared.exception.RefreshTokenException;
import com.trophix.api.users.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CompleteRegistrationUseCase completeRegistrationUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshSessionUseCase refreshSessionUseCase;
    private final LogoutUseCase logoutUseCase;
    private final AuthWebMapper authWebMapper;
    private final AuthCookieManager authCookieManager;
    private final ClientMetadataReader clientMetadataReader;

    @PostMapping("/register-completion")
    public ResponseEntity<UserResponse> completeRegistration(
            @Valid @RequestBody RegistrationRequest request) {
        User user = completeRegistrationUseCase.completeRegistration(
                authWebMapper.toRegistrationCommand(request));
        UserResponse response = authWebMapper.toUserResponse(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request,
                                      HttpServletRequest httpRequest) {
        ClientMetadataReader.ClientMetadata metadata = clientMetadataReader.read(httpRequest);
        AuthTokens tokens = loginUseCase.login(
                authWebMapper.toLoginCommand(request, metadata.ipAddress(), metadata.userAgent()));
        return ResponseEntity.ok()
                .headers(authCookieManager.sessionHeaders(tokens))
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(HttpServletRequest httpRequest) {
        String rawRefreshToken = authCookieManager.extractRefreshToken(httpRequest)
                .orElseThrow(() -> new RefreshTokenException("Sessão expirada. Faça login novamente."));
        ClientMetadataReader.ClientMetadata metadata = clientMetadataReader.read(httpRequest);
        AuthTokens tokens = refreshSessionUseCase.refresh(
                authWebMapper.toRefreshCommand(rawRefreshToken, metadata.ipAddress(), metadata.userAgent()));
        return ResponseEntity.ok()
                .headers(authCookieManager.sessionHeaders(tokens))
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest) {
        authCookieManager.extractRefreshToken(httpRequest)
                .ifPresent(raw -> logoutUseCase.logout(authWebMapper.toLogoutCommand(raw)));
        return ResponseEntity.ok()
                .headers(authCookieManager.logoutHeaders())
                .build();
    }
}
