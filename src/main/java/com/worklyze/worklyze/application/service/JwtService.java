package com.worklyze.worklyze.application.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.worklyze.worklyze.domain.entity.User;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    private final String SECRET = "minha-chave-super-secreta";

    public String generateToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        return JWT.create()
                .withSubject(user.getEmail())
                .withClaim("provider", user.getTypeProvider().getId())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 86400000)) // 1 dia
                .sign(algorithm);
    }
}