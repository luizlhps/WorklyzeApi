package com.worklyze.worklyze.application.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.worklyze.worklyze.application.dto.AuthRefresh;
import com.worklyze.worklyze.application.dto.AuthRequest;
import com.worklyze.worklyze.application.dto.AuthResponse;
import com.worklyze.worklyze.domain.entity.TypeProvider;
import com.worklyze.worklyze.domain.entity.User;
import com.worklyze.worklyze.domain.enums.TypeProviderEnum;
import com.worklyze.worklyze.infra.repository.UserRepository;
import com.worklyze.worklyze.shared.auth.AuthExceptionCode;
import com.worklyze.worklyze.shared.auth.PasswordHash;
import com.worklyze.worklyze.shared.exceptions.UnauthorizedRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.file.attribute.UserPrincipal;

import static com.worklyze.worklyze.shared.auth.AuthExceptionCode.USUARIO_EMAIL_SENHA_INVALIDO;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public AuthResponse register(AuthRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        User user = new User();
        user.setEmail(request.email());

        var passwordHash = PasswordHash.hash(request.password());
        user.setPassword(passwordHash);

        TypeProvider typeProvider = new TypeProvider();
        typeProvider.setId(TypeProviderEnum.LOCAL.getValue());
        user.setTypeProvider(typeProvider);

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedRequestException(USUARIO_EMAIL_SENHA_INVALIDO.getMessage(), USUARIO_EMAIL_SENHA_INVALIDO.getCode()));

        boolean isPasswordCorrect = PasswordHash.checkPassword(user.getPassword());

        if (!isPasswordCorrect) {
            throw new UnauthorizedRequestException("Senha ou Email incorretos", null);
        }

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(AuthRefresh request) {
        String email = null;

        try {
            email = jwtService.getEmailFromToken(request.refreshToken());
        } catch (JWTVerificationException ex) {
            throw new UnauthorizedRequestException("Refresh Token Inválido", null);
        }

        var user = userRepository.findByEmail(email).orElseThrow(() -> new UnauthorizedRequestException("Usuário não encontrado", null));

        if (!user.getRefreshToken().equals(request.refreshToken())) {
            throw new UnauthorizedRequestException("Refresh Token Inválido", null);
        }

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        var userSave = userRepository.save(user);

        return new AuthResponse(accessToken, refreshToken);
    }
}