package com.worklyze.worklyze.adapter.web;

import com.worklyze.worklyze.application.dto.auth.AuthRefresh;
import com.worklyze.worklyze.application.dto.auth.AuthRegister;
import com.worklyze.worklyze.application.dto.auth.AuthRequest;
import com.worklyze.worklyze.application.dto.auth.AuthResponse;
import com.worklyze.worklyze.application.service.AuthService;
import com.worklyze.worklyze.shared.exceptions.UnauthorizedRequestException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid AuthRegister request, HttpServletResponse response) {
        AuthResponse authResponse = authService.register(request);

        addCookiesInResponse(response, authResponse.token(), authResponse.refreshToken());

        var authWithoutRefreshToken = new AuthResponse(authResponse.token(), null);

        return ResponseEntity.status(HttpStatus.CREATED).body(authWithoutRefreshToken);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);

        addCookiesInResponse(response, authResponse.token(), authResponse.refreshToken());
        var authWithoutRefreshToken = new AuthResponse(authResponse.token(), null);
        return ResponseEntity.ok(authWithoutRefreshToken);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh( HttpServletRequest request, HttpServletResponse response) {
        AuthRefresh requestDto = null;

        Optional<Cookie> refreshTokenCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("refreshToken"))
                .findFirst();

        if (refreshTokenCookie.isPresent()) {
            requestDto = new AuthRefresh(refreshTokenCookie.get().getValue());

        } else {
            throw new UnauthorizedRequestException("Refresh Token Inválido", null);
        }

        var authResponse = authService.refresh(requestDto);

        addCookiesInResponse(response, authResponse.token(), authResponse.refreshToken());
        var authWithoutRefreshToken = new AuthResponse(authResponse.token(), null);
        return ResponseEntity.ok(authWithoutRefreshToken);
    }

    private static void addCookiesInResponse(HttpServletResponse response, String token, String refreshToken) {
        Cookie accessTokenCookie = new Cookie("token", token);
        accessTokenCookie.setHttpOnly(false);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }
}