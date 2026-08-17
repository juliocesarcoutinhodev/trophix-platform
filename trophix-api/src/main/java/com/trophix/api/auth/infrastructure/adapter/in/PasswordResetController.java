package com.trophix.api.auth.infrastructure.adapter.in;

import com.trophix.api.auth.application.ports.in.ForgotPasswordUseCase;
import com.trophix.api.auth.application.ports.in.ResetPasswordUseCase;
import com.trophix.api.auth.infrastructure.adapter.in.dto.ForgotPasswordRequest;
import com.trophix.api.shared.dto.MessageResponse;
import com.trophix.api.auth.infrastructure.adapter.in.dto.ResetPasswordRequest;
import com.trophix.api.auth.infrastructure.adapter.in.mapper.AuthWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PasswordResetController {

    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final AuthWebMapper authWebMapper;

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        forgotPasswordUseCase.requestReset(authWebMapper.toForgotPasswordCommand(request));
        return ResponseEntity.ok(new MessageResponse(
                "Se o e-mail informado estiver cadastrado, você receberá um link de redefinição de senha."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        resetPasswordUseCase.resetPassword(authWebMapper.toResetPasswordCommand(request));
        return ResponseEntity.ok(new MessageResponse("Senha redefinida com sucesso! Faça login novamente."));
    }
}
