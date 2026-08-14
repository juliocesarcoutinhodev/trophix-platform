package com.trophix.api.reports.infrastructure.adapter.in.dto;

import com.trophix.api.reports.model.ReportTargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SubmitReportRequest(
        @NotNull(message = "Informe o tipo de denúncia") ReportTargetType targetType,
        @NotNull(message = "Informe o alvo da denúncia") UUID targetId,
        @NotBlank @Size(max = 500, message = "O motivo deve ter no máximo 500 caracteres") String reason) {
}
