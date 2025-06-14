package com.worklyze.worklyze.infra.config.security;

public class JwtTokenInvalidException extends RuntimeException {
    public JwtTokenInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}