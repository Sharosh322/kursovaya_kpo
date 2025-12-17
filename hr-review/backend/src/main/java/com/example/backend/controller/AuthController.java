package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.entity.AppUser;
import com.example.backend.entity.Role;
import com.example.backend.repository.AppUserRepository;
import com.example.backend.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AppUserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService,
                          AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        AppUser user = new AppUser();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role role;
        String roleFromReq = request.getRole();
        if (roleFromReq == null || roleFromReq.isBlank()) {
            role = Role.USER;
        } else {
            try {
                role = Role.valueOf(roleFromReq.toUpperCase());
            } catch (IllegalArgumentException ex) {
                role = Role.USER;
            }
        }
        user.setRole(role);

        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        AppUser user = (AppUser) auth.getPrincipal();

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getFullName()
        );

        Cookie cookie = new Cookie("JWT", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // ✅ true только если HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1 час

        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/auth/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0); // удалить

        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/auth/me")
    public ResponseEntity<AuthMeDto> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof AppUser user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(new AuthMeDto(
                user.getEmail(),
                user.getFullName(),
                user.getRole().name()
        ));
    }
}
