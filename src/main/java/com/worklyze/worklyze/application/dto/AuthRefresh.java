package com.worklyze.worklyze.application.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRefresh(@NotBlank String refreshToken) {}