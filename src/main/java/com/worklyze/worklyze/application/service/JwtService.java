package com.worklyze.worklyze.application.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.worklyze.worklyze.domain.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String SECRET;


    @Value("${jwt.exp}")
    private Integer EXPIRATION_IN_SECONDS;



    public String generateToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(EXPIRATION_IN_SECONDS);

        return JWT.create()
                .withSubject(user.getEmail())
                .withClaim("provider", user.getTypeProvider().getId())
                .withClaim("uuid", user.getId().toString())
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(exp))
                .sign(algorithm);
    }

    public String generateRefreshToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        Instant now = Instant.now();
        Instant exp = now.plus(14, ChronoUnit.DAYS);

        return JWT.create()
                .withSubject(user.getEmail())
                .withClaim("type", "refresh")
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(exp))
                .sign(algorithm);
    }

    public DecodedJWT decodedJWT(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        return JWT.require(algorithm).build().verify(token);
    }

}