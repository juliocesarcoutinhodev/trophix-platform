package com.trophix.api.auth.infrastructure.adapter.in;

import com.trophix.api.auth.application.ports.in.CompleteRegistrationUseCase;
import com.trophix.api.auth.application.ports.in.LoginUseCase;
import com.trophix.api.auth.infrastructure.adapter.in.cookie.JwtCookieManager;
import com.trophix.api.auth.infrastructure.adapter.in.dto.LoginRequest;
import com.trophix.api.auth.infrastructure.adapter.in.dto.RegistrationRequest;
import com.trophix.api.auth.infrastructure.adapter.in.dto.UserResponse;
import com.trophix.api.auth.infrastructure.adapter.in.mapper.AuthWebMapper;
import com.trophix.api.users.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
    private final AuthWebMapper authWebMapper;
    private final JwtCookieManager jwtCookieManager;

    @PostMapping("/register-completion")
    public ResponseEntity<UserResponse> completeRegistration(
            @Valid @RequestBody RegistrationRequest request) {
        User user = completeRegistrationUseCase.completeRegistration(
                authWebMapper.toRegistrationCommand(request));
        UserResponse response = authWebMapper.toUserResponse(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request) {
        String token = loginUseCase.login(authWebMapper.toLoginCommand(request));
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookieManager.generateJwtCookie(token).toString())
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookieManager.generateLogoutCookie().toString())
                .build();
    }
}