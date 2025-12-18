package com.example.backend.security;

import com.example.backend.entity.AppUser;
import com.example.backend.entity.Role;
import com.example.backend.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    private AppUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new AppUserDetailsService(appUserRepository);
    }

    @Test
    void loadUserByUsername_ValidEmail_ReturnsUserDetails() {
        // Arrange
        String email = "user@example.com";
        AppUser appUser = new AppUser(email, "encodedPassword", Role.USER);
        appUser.setId(1L);
        appUser.setFullName("Test User");
        
        when(appUserRepository.findByEmail(email)).thenReturn(Optional.of(appUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        
        verify(appUserRepository).findByEmail(email);
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        // Arrange
        String email = "nonexistent@example.com";
        when(appUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> userDetailsService.loadUserByUsername(email)
        );
        
        assertEquals("User not found: " + email, exception.getMessage());
        verify(appUserRepository).findByEmail(email);
    }

    @Test
    void loadUserByUsername_AdminUser_ReturnsAdminRole() {
        // Arrange
        String email = "admin@example.com";
        AppUser appUser = new AppUser(email, "encodedPassword", Role.ADMIN);
        
        when(appUserRepository.findByEmail(email)).thenReturn(Optional.of(appUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Assert
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }
}