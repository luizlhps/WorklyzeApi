package com.worklyze.worklyze.shared.auth;

import lombok.Getter;

@Getter
public enum AuthExceptionCode {
    USUARIO_EMAIL_SENHA_INVALIDO("Usuário ou senha inválidos", "USUARIO_EMAIL_SENHA_INVALIDO"),
    ;

    private String message;
    private String code;

    AuthExceptionCode(String message, String code) {
        this.message = message;
        this.code = code;
    }

}
