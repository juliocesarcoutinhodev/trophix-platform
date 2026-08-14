package com.trophix.api.admin.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record UpdateUserRolesRequest(
        @NotEmpty(message = "Informe ao menos um cargo") Set<String> roles) {
}
