package com.josemiguelhyb.fastbank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Expone un PasswordEncoder (BCrypt) sin activar Spring Security completo.
 * Usado por UserServiceImpl para hashear contraseñas al crear/actualizar usuarios.
 */
@Configuration
public class SecurityCryptoConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Fuerza por defecto (10) es adecuada. Se puede subir (11-12) si el hardware lo permite.
        return new BCryptPasswordEncoder();
    }
}
