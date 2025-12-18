package com.example.backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Test
    void passwordEncoder_BeanCreated() {
        // Act
        var passwordEncoder = securityConfig.passwordEncoder();

        // Assert
        assertNotNull(passwordEncoder);
    }

    @Test
    void corsConfigurationSource_BeanCreated() {
        // Act
        var corsConfig = securityConfig.corsConfigurationSource();

        // Assert
        assertNotNull(corsConfig);
    }
}