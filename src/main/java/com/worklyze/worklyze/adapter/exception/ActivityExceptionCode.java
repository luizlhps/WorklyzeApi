package com.worklyze.worklyze.adapter.exception;

import lombok.Getter;

@Getter
public enum ActivityExceptionCode {
    ACTIVITY_NOT_FOUND("Atividade não encontrada.", "ACTIVITY_NOT_FOUND"),
    ACTIVITY_ALREADY_ACTIVITY("Já existe uma atividade iniciada", "ACTIVITY_ALREADY_ACTIVITY"),
    ;

    private String message;
    private String code;

    ActivityExceptionCode(String message, String code) {
        this.message = message;
        this.code = code;
    }

}
