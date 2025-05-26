package com.worklyze.worklyze.infra.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {
    @Value("${cors.allowedOrigin}")
    private String allowedOrigin;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        config.setAllowCredentials(true);

        var origins = allowedOrigin.split(",");

        config.setAllowedOriginPatterns(List.of(origins));

        config.addAllowedMethod("*");

        config.addAllowedHeader("*");

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
