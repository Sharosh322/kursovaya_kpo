package com.example.backend.security;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void generateToken_ValidData_ReturnsToken() {
        // Act
        String token = jwtService.generateToken("test@example.com", "ADMIN", "John Doe");

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_ValidToken_ReturnsUsername() {
        // Arrange
        String token = jwtService.generateToken("test@example.com", "ADMIN", "John Doe");

        // Act
        String username = jwtService.extractUsername(token);

        // Assert
        assertEquals("test@example.com", username);
    }

    @Test
    void extractRole_ValidToken_ReturnsRole() {
        // Arrange
        String token = jwtService.generateToken("test@example.com", "ADMIN", "John Doe");

        // Act
        String role = jwtService.extractRole(token);

        // Assert
        assertEquals("ADMIN", role);
    }

    @Test
    void extractFullName_ValidToken_ReturnsFullName() {
        // Arrange
        String token = jwtService.generateToken("test@example.com", "ADMIN", "John Doe");

        // Act
        String fullName = jwtService.extractFullName(token);

        // Assert
        assertEquals("John Doe", fullName);
    }

    @Test
    void isTokenValid_ValidTokenAndUsername_ReturnsTrue() {
        // Arrange
        String token = jwtService.generateToken("test@example.com", "ADMIN", "John Doe");

        // Act & Assert
        assertTrue(jwtService.isTokenValid(token, "test@example.com"));
    }

    @Test
    void isTokenValid_DifferentUsername_ReturnsFalse() {
        // Arrange
        String token = jwtService.generateToken("test@example.com", "ADMIN", "John Doe");

        // Act & Assert
        assertFalse(jwtService.isTokenValid(token, "other@example.com"));
    }
}