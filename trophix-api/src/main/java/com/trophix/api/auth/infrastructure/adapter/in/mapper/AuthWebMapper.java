package com.trophix.api.auth.infrastructure.adapter.in.mapper;

import com.trophix.api.auth.application.ports.in.CompleteRegistrationUseCase;
import com.trophix.api.auth.application.ports.in.ForgotPasswordUseCase;
import com.trophix.api.auth.application.ports.in.LoginUseCase;
import com.trophix.api.auth.application.ports.in.ResetPasswordUseCase;
import com.trophix.api.auth.infrastructure.adapter.in.dto.ForgotPasswordRequest;
import com.trophix.api.auth.infrastructure.adapter.in.dto.LoginRequest;
import com.trophix.api.auth.infrastructure.adapter.in.dto.RegistrationRequest;
import com.trophix.api.auth.infrastructure.adapter.in.dto.ResetPasswordRequest;
import com.trophix.api.auth.infrastructure.adapter.in.dto.UserResponse;
import com.trophix.api.shared.model.Role;
import com.trophix.api.users.model.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Converts inbound web DTOs into application-layer commands and responses,
 * keeping the controller free of manual instantiation.
 */
@Component
public class AuthWebMapper {

    public CompleteRegistrationUseCase.RegistrationCommand toRegistrationCommand(RegistrationRequest request) {
        return new CompleteRegistrationUseCase.RegistrationCommand(
                request.psnId(), request.email(), request.password());
    }

    public LoginUseCase.LoginCommand toLoginCommand(LoginRequest request, String ipAddress, String userAgent) {
        return new LoginUseCase.LoginCommand(request.email(), request.password(), ipAddress, userAgent);
    }

    public ForgotPasswordUseCase.ForgotPasswordCommand toForgotPasswordCommand(ForgotPasswordRequest request) {
        return new ForgotPasswordUseCase.ForgotPasswordCommand(request.email());
    }

    public ResetPasswordUseCase.ResetPasswordCommand toResetPasswordCommand(ResetPasswordRequest request) {
        return new ResetPasswordUseCase.ResetPasswordCommand(request.token(), request.newPassword());
    }

    public UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.id(),
                user.username(),
                user.email(),
                user.avatarUrl(),
                user.roles().stream()
                        .map(Role::name)
                        .collect(Collectors.toSet()));
    }
}