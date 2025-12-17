package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.entity.AppUser;
import com.example.backend.service.HrService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class CandidateController {

    private final HrService hrService;

    public CandidateController(HrService hrService) {
        this.hrService = hrService;
    }

    // кандидаты вакансии:
    // ADMIN -> все, USER -> только назначенные ему (в этой вакансии)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/vacancies/{vacancyId}/candidates")
    public List<CandidateListItemDto> getCandidates(
            @PathVariable Long vacancyId,
            @AuthenticationPrincipal AppUser currentUser
    ) {
        return hrService.getCandidatesForVacancy(vacancyId, currentUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/vacancies/{vacancyId}/candidates")
    public ResponseEntity<CandidateListItemDto> createCandidate(
            @PathVariable Long vacancyId,
            @RequestBody CandidateCreateRequest request
    ) {
        CandidateListItemDto created = hrService.createCandidate(vacancyId, request);
        return ResponseEntity.ok(created);
    }

    // детали кандидата: ADMIN или назначенный USER
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/candidates/{candidateId}")
    public ResponseEntity<CandidateDetailsDto> getCandidate(
            @PathVariable Long candidateId,
            @AuthenticationPrincipal AppUser currentUser
    ) {
        CandidateDetailsDto details = hrService.getCandidateDetails(candidateId, currentUser);
        return ResponseEntity.ok(details);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/api/candidates/{candidateId}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long candidateId,
            @RequestBody StatusUpdateRequest request
    ) {
        hrService.updateCandidateStatus(candidateId, request.getStatus());
        return ResponseEntity.noContent().build();
    }

    // отзыв: ADMIN или назначенный USER
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/api/candidates/{candidateId}/reviews")
    public ResponseEntity<ReviewDto> addReview(
            @PathVariable Long candidateId,
            @RequestBody ReviewCreateRequest request,
            @RequestHeader(value = "X-Author", required = false) String authorHeader,
            @AuthenticationPrincipal AppUser currentUser
    ) {
        ReviewDto created = hrService.addReview(candidateId, request.getText(), authorHeader, currentUser);
        return ResponseEntity.ok(created);
    }

    // список кандидатов:
    // ADMIN -> все, USER -> только назначенные
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/candidates")
    public List<CandidateListItemDto> getAllCandidates(@AuthenticationPrincipal AppUser currentUser) {
        return hrService.getAllCandidatesForCurrentUser(currentUser);
    }

    // назначение кандидата интервьюеру (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/candidates/{candidateId}/assign")
    public ResponseEntity<Void> assign(
            @PathVariable Long candidateId,
            @RequestBody AssignInterviewerRequest request
    ) {
        hrService.assignInterviewer(candidateId, request.getInterviewerEmail());
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/candidates/my")
    public ResponseEntity<List<CandidateListItemDto>> getMyCandidates(
    @AuthenticationPrincipal AppUser currentUser
    ) {
    List<CandidateListItemDto> candidates = hrService.getAllCandidatesForCurrentUser(currentUser);
    return ResponseEntity.ok(candidates);
}
}
