package com.rrom.task_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().authenticated()  // All other endpoints require authentication
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.decoder(jwtDecoder()))); // Enable JWT authentication for the resource server

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Assuming you're using JWT tokens, configure your JWT decoder here
        return JwtDecoders.fromIssuerLocation("http://localhost:8083/realms/rrom-realm"); // Replace with your issuer URL
    }
}