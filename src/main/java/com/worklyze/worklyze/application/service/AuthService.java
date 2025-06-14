package com.worklyze.worklyze.application.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.worklyze.worklyze.application.dto.auth.AuthRefresh;
import com.worklyze.worklyze.application.dto.auth.AuthRegister;
import com.worklyze.worklyze.application.dto.auth.AuthRequest;
import com.worklyze.worklyze.application.dto.auth.AuthResponse;
import com.worklyze.worklyze.domain.entity.TypeProvider;
import com.worklyze.worklyze.domain.entity.User;
import com.worklyze.worklyze.domain.enums.TypeProviderEnum;
import com.worklyze.worklyze.domain.interfaces.repository.UserRepository;
import com.worklyze.worklyze.shared.auth.PasswordHash;
import com.worklyze.worklyze.shared.exceptions.UnauthorizedRequestException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import static com.worklyze.worklyze.shared.auth.AuthExceptionCode.USUARIO_EMAIL_SENHA_INVALIDO;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;

    public AuthService(UserRepository userRepository, JwtService jwtService, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public AuthResponse register(AuthRegister request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        User user = modelMapper.map(request, User.class);

        var passwordHash = PasswordHash.hash(request.getPassword());
        user.setPassword(passwordHash);

        TypeProvider typeProvider = new TypeProvider();
        typeProvider.setId(TypeProviderEnum.LOCAL.getValue());
        user.setTypeProvider(typeProvider);

        var refreshToken = jwtService.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);

        user = userRepository.create(user);

        var accessToken = jwtService.generateToken(user);

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
        userRepository.update(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(AuthRefresh request) {
        String email = null;

        try {
            var decodedJWT = jwtService.decodedJWT(request.refreshToken());
            email = decodedJWT.getSubject();
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
        var userSave = userRepository.update(user);

        return new AuthResponse(accessToken, refreshToken);
    }
}