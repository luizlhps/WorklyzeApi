package com.worklyze.worklyze.infra.config.security;

import com.worklyze.worklyze.infra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {


/*
    private final JwtFilter jwtFilter;

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(JwtFilter jwtFilter, CustomUserDetailsService customUserDetailsService) {
        this.jwtFilter = jwtFilter;
        this.customUserDetailsService = customUserDetailsService;
    }
*/


    //@Profile("dev")
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs*/**");
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http, UserRepository userRepository) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        // Route protected
        http.authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.
                                requestMatchers("/error").permitAll()
                                .requestMatchers("/api/auth/**", "/oauth2/**").permitAll()
                                .requestMatchers("/**").authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint())
                ).oauth2Login(oauth -> oauth
                        .loginPage("/api/auth/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService(userRepository)))
                        .successHandler(oAuth2SuccessHandler())
                )
                .cors(Customizer.withDefaults());

        // Set session management to stateless
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );



        return http.build();
    }

    @Bean
    AuthenticationEntryPoint authenticationEntryPoint() {
        return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    }

    @Bean
    public CustomAuth2Service customOAuth2UserService(UserRepository userRepository) {
        return new CustomAuth2Service(userRepository);
    }

    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler();
    }

}