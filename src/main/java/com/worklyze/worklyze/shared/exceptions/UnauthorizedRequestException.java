package com.worklyze.worklyze.shared.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UnauthorizedRequestException extends CustomException {
    private final HttpStatus status;
    private final String code;

    public UnauthorizedRequestException(String message, String code) {
        super(message);
        this.status = HttpStatus.valueOf(401);
        this.code = code;
    }
}
