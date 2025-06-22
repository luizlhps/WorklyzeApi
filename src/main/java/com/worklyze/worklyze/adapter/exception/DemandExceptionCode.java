package com.worklyze.worklyze.adapter.exception;

import lombok.Getter;

@Getter
public enum DemandExceptionCode {
    DEMAND_NOT_FOUND("Demanda não encontrada.", "DEMAND_NOT_FOUND"),
    ;

    private String message;
    private String code;

    DemandExceptionCode(String message, String code) {
        this.message = message;
        this.code = code;
    }

}
