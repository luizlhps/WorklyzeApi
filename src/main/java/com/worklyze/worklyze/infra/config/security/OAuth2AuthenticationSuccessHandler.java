package com.worklyze.worklyze.infra.config.security;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        // Aqui você pode pegar o principal, gerar JWT, redirecionar etc.
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Autenticação via OAuth2 realizada com sucesso");
    }
}