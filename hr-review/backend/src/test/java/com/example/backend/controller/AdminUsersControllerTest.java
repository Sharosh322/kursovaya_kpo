package com.example.backend.controller;

import com.example.backend.dto.UserListItemDto;
import com.example.backend.entity.AppUser;
import com.example.backend.entity.Role;
import com.example.backend.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUsersControllerTest {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private AdminUsersController adminUsersController;

    private List<AppUser> mockUsers;

    @BeforeEach
    void setUp() {
        AppUser admin = new AppUser();
        admin.setId(1L);
        admin.setEmail("admin@example.com");
        admin.setFullName("Admin User");
        admin.setRole(Role.ADMIN);

        AppUser interviewer1 = new AppUser();
        interviewer1.setId(2L);
        interviewer1.setEmail("interviewer1@example.com");
        interviewer1.setFullName("Interviewer One");
        interviewer1.setRole(Role.USER);

        AppUser interviewer2 = new AppUser();
        interviewer2.setId(3L);
        interviewer2.setEmail("interviewer2@example.com");
        interviewer2.setFullName("Interviewer Two");
        interviewer2.setRole(Role.USER);

        mockUsers = List.of(admin, interviewer1, interviewer2);
    }

    @Test
    void getInterviewers_ReturnsOnlyUsersWithUserRole() {
        // Arrange
        when(appUserRepository.findAll()).thenReturn(mockUsers);

        // Act
        List<UserListItemDto> result = adminUsersController.getInterviewers();

        // Assert
        assertEquals(2, result.size()); // Только USER роли
        
        // Проверяем, что ADMIN не включен
        assertTrue(result.stream().noneMatch(u -> u.getEmail().equals("admin@example.com")));
        
        // Проверяем, что USER роли включены
        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("interviewer1@example.com")));
        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("interviewer2@example.com")));
        
        verify(appUserRepository).findAll();
    }

    @Test
    void getInterviewers_EmptyRepository_ReturnsEmptyList() {
        // Arrange
        when(appUserRepository.findAll()).thenReturn(List.of());

        // Act
        List<UserListItemDto> result = adminUsersController.getInterviewers();

        // Assert
        assertTrue(result.isEmpty());
        verify(appUserRepository).findAll();
    }

    @Test
    void getInterviewers_OnlyAdmins_ReturnsEmptyList() {
        // Arrange
        AppUser admin1 = new AppUser();
        admin1.setRole(Role.ADMIN);
        AppUser admin2 = new AppUser();
        admin2.setRole(Role.ADMIN);
        
        when(appUserRepository.findAll()).thenReturn(List.of(admin1, admin2));

        // Act
        List<UserListItemDto> result = adminUsersController.getInterviewers();

        // Assert
        assertTrue(result.isEmpty());
    }
}