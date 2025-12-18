package com.example.backend.security;

import com.example.backend.entity.AppUser;
import com.example.backend.entity.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AppUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void setUp() {
        jwtAuthFilter = new JwtAuthFilter(jwtService, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ValidTokenInHeader_SetsAuthentication() throws Exception {
        // Arrange
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn("user@example.com");
        
        AppUser user = new AppUser("user@example.com", "password", Role.USER);
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(user);
        when(jwtService.isTokenValid(token, "user@example.com")).thenReturn(true);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("user@example.com", 
            SecurityContextHolder.getContext().getAuthentication().getName());
        
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ValidTokenInCookie_SetsAuthentication() throws Exception {
        // Arrange
        String token = "valid.jwt.token";
        Cookie cookie = new Cookie("JWT", token);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(jwtService.extractUsername(token)).thenReturn("user@example.com");
        
        AppUser user = new AppUser("user@example.com", "password", Role.USER);
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(user);
        when(jwtService.isTokenValid(token, "user@example.com")).thenReturn(true);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_InvalidToken_ContinuesFilterChain() throws Exception {
        // Arrange
        String token = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenThrow(new RuntimeException("Invalid token"));

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_NoToken_ContinuesFilterChain() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotFilter_LoginPath_ReturnsTrue() throws Exception {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/auth/login");

        // Act
        boolean result = jwtAuthFilter.shouldNotFilter(request);

        // Assert
        assertTrue(result);
    }

    @Test
    void shouldNotFilter_RegisterPath_ReturnsTrue() throws Exception {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/auth/register");

        // Act
        boolean result = jwtAuthFilter.shouldNotFilter(request);

        // Assert
        assertTrue(result);
    }

    @Test
    void shouldNotFilter_OtherPath_ReturnsFalse() throws Exception {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/vacancies");

        // Act
        boolean result = jwtAuthFilter.shouldNotFilter(request);

        // Assert
        assertFalse(result);
    }
}