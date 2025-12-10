package com.example.backend.controller;

import com.example.backend.dto.RegisterRequest;
import com.example.backend.dto.JwtResponse;
import com.example.backend.entity.AppUser;
import com.example.backend.entity.Role;
import com.example.backend.repository.AppUserRepository;
import com.example.backend.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(AppUserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@RequestBody RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        AppUser user = new AppUser();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // выбор роли из запроса
        Role role;
        String roleFromReq = request.getRole();

        if (roleFromReq == null || roleFromReq.isBlank()) {
            role = Role.USER; // дефолт
        } else {
            try {
                role = Role.valueOf(roleFromReq.toUpperCase());
            } catch (IllegalArgumentException ex) {
                role = Role.USER; // если прислали чушь
            }
        }

        user.setRole(role);

        userRepository.save(user);

        // JwtService.generateToken ожидает String (username/email)
        String token = jwtService.generateToken(user.getUsername());

        return ResponseEntity.ok(new JwtResponse(token));
    }
}
