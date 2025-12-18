package com.example.backend.repository;

import com.example.backend.entity.AppUser;
import com.example.backend.entity.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserRepositoryTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Test
    void findByEmail_ExistingEmail_ReturnsUser() {
        // Arrange
        AppUser user = new AppUser();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFullName("Test User");
        user.setRole(Role.USER);

        when(appUserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        Optional<AppUser> result = appUserRepository.findByEmail("test@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(appUserRepository).findByEmail("test@example.com");
    }

    @Test
    void existsByEmail_ExistingEmail_ReturnsTrue() {
        // Arrange
        when(appUserRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act
        boolean exists = appUserRepository.existsByEmail("existing@example.com");

        // Assert
        assertTrue(exists);
        verify(appUserRepository).existsByEmail("existing@example.com");
    }

    @Test
    void save_ValidUser_ReturnsSavedUser() {
        // Arrange
        AppUser user = new AppUser();
        user.setEmail("new@example.com");
        user.setPassword("password");
        user.setFullName("New User");
        user.setRole(Role.USER);

        AppUser savedUser = new AppUser();
        savedUser.setId(1L);
        savedUser.setEmail("new@example.com");
        savedUser.setPassword("password");
        savedUser.setFullName("New User");
        savedUser.setRole(Role.USER);

        when(appUserRepository.save(user)).thenReturn(savedUser);

        // Act
        AppUser result = appUserRepository.save(user);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("new@example.com", result.getEmail());
        verify(appUserRepository).save(user);
    }
}