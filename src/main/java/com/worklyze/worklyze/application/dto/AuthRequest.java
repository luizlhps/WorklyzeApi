package com.worklyze.worklyze.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank String email,
        @NotBlank String password
) {
}