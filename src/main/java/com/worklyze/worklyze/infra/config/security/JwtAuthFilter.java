package com.worklyze.worklyze.infra.config.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.worklyze.worklyze.application.service.JwtService;
import com.worklyze.worklyze.domain.entity.User;
import com.worklyze.worklyze.domain.interfaces.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public JwtAuthFilter(JwtService jwtService,
                         UserRepository userRepository,
                         AuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {
        String path = req.getServletPath();

        if (path.startsWith("/v1/auth")
                || path.startsWith("/oauth2")) {
            chain.doFilter(req, res);
            return;
        }

        String header = req.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                // valida e decodifica
                DecodedJWT jwt = jwtService.decodedJWT(token);
                String email = jwt.getSubject();

                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                CustomUserPrincipal principal = new CustomUserPrincipal(user);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (TokenExpiredException e) {
                SecurityContextHolder.clearContext();
                authenticationEntryPoint.commence(req, res,
                        new InsufficientAuthenticationException("Token expired", e));
                return;
            } catch (JWTVerificationException | UsernameNotFoundException e) {
                SecurityContextHolder.clearContext();
                authenticationEntryPoint.commence(req, res,
                        new InsufficientAuthenticationException("Invalid token", e));
                return;
            }
        }

        chain.doFilter(req, res);
    }
}