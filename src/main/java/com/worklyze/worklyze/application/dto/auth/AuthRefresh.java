package com.worklyze.worklyze.application.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthRefresh(@NotBlank String refreshToken) {}