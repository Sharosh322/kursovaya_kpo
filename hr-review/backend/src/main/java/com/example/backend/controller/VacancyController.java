package com.example.backend.controller;

import com.example.backend.dto.VacancyCreateRequest;
import com.example.backend.dto.VacancyDto;
import com.example.backend.service.HrService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vacancies")
@CrossOrigin(origins = "http://localhost:5173")
public class VacancyController {

    private final HrService hrService;

    public VacancyController(HrService hrService) {
        this.hrService = hrService;
    }

    @GetMapping
    public List<VacancyDto> getAll() {
        return hrService.getAllVacancies();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<VacancyDto> create(@RequestBody VacancyCreateRequest request) {
        VacancyDto created = hrService.createVacancy(request);
        return ResponseEntity.ok(created);
    }
}
