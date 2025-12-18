package com.example.backend.controller;

import com.example.backend.dto.UserListItemDto;
import com.example.backend.repository.AppUserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/admin")
public class AdminUsersController {

    private final AppUserRepository appUserRepository;

    public AdminUsersController(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    // Список интервьюеров (role = USER)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/interviewers")
    public List<UserListItemDto> getInterviewers() {
        return appUserRepository.findAll()
                .stream()
                .filter(u -> u.getRole() != null && u.getRole().name().equals("USER"))
                .map(u -> new UserListItemDto(u.getId(), u.getEmail(), u.getFullName(), u.getRole().name()))
                .toList();
    }
}
