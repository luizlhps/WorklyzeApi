package com.worklyze.worklyze.application.service;

import com.worklyze.worklyze.application.dto.AuthRequest;
import com.worklyze.worklyze.application.dto.AuthResponse;
import com.worklyze.worklyze.domain.entity.TypeProvider;
import com.worklyze.worklyze.domain.entity.User;
import com.worklyze.worklyze.domain.enums.TypeProviderEnum;
import com.worklyze.worklyze.infra.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public AuthResponse register(AuthRequest request) {
        if (userRepository.findByEmailOrUsername(request.email(), null).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setPassword("batata");

        TypeProvider typeProvider = new TypeProvider();
        typeProvider.setId(TypeProviderEnum.LOCAL.getValue());
        user.setTypeProvider(typeProvider);

        userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(user));
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmailOrUsername(request.email(), null)
                .orElseThrow(() -> new UsernameNotFoundException("Email não encontrado"));
        return new AuthResponse(jwtService.generateToken(user));
    }
}