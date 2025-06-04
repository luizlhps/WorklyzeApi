package com.worklyze.worklyze.application.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRegister(
        @NotBlank String email,
        @NotBlank String password,
        @NotBlank String name,
        @NotBlank String surname
) {
}