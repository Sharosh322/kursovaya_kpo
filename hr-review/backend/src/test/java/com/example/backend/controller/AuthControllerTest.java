package com.example.backend.controller;

import com.example.backend.entity.AppUser;
import com.example.backend.entity.Role;
import com.example.backend.repository.AppUserRepository;
import com.example.backend.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_ValidRequest_ReturnsOk() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFullName("Test User");
        request.setRole("USER");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        // Act
        ResponseEntity<Void> result = authController.register(request);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(userRepository).save(any(AppUser.class));
    }

    @Test
    void register_ExistingEmail_ReturnsConflict() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act
        ResponseEntity<Void> result = authController.register(request);

        // Assert
        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_ValidCredentials_SetsCookieAndReturnsOk() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        AppUser user = new AppUser();
        user.setEmail("test@example.com");
        user.setRole(Role.USER);
        user.setFullName("Test User");

        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.generateToken("test@example.com", "USER", "Test User"))
            .thenReturn("jwt-token");

        // Act
        ResponseEntity<Void> result = authController.login(request, response);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(response).addCookie(any(Cookie.class));
        verify(jwtService).generateToken("test@example.com", "USER", "Test User");
    }

    @Test
    void logout_ClearsCookieAndReturnsOk() {
        // Act
        ResponseEntity<Void> result = authController.logout(response);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(response).addCookie(any(Cookie.class));
    }
}