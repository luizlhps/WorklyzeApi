package com.worklyze.worklyze.adapter.exception;

import lombok.Getter;

@Getter
public enum TaskExceptionCode {
    TASK_NOT_FOUND("Tarefa não encontrada.", "TASK_NOT_FOUND"),
    ;

    private String message;
    private String code;

    TaskExceptionCode(String message, String code) {
        this.message = message;
        this.code = code;
    }

}
