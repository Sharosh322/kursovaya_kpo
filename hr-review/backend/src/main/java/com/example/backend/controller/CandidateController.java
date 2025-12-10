package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.service.HrService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class CandidateController {

    private final HrService hrService;

    public CandidateController(HrService hrService) {
        this.hrService = hrService;
    }

    @GetMapping("/api/vacancies/{vacancyId}/candidates")
    public List<CandidateListItemDto> getCandidates(@PathVariable Long vacancyId) {
        return hrService.getCandidatesForVacancy(vacancyId);
    }

    @PostMapping("/api/vacancies/{vacancyId}/candidates")
    public ResponseEntity<CandidateListItemDto> createCandidate(
            @PathVariable Long vacancyId,
            @RequestBody CandidateCreateRequest request
    ) {
        CandidateListItemDto created = hrService.createCandidate(vacancyId, request);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/api/candidates/{candidateId}")
    public ResponseEntity<CandidateDetailsDto> getCandidate(@PathVariable Long candidateId) {
        CandidateDetailsDto details = hrService.getCandidateDetails(candidateId);
        return ResponseEntity.ok(details);
    }

    @PatchMapping("/api/candidates/{candidateId}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long candidateId,
            @RequestBody StatusUpdateRequest request
    ) {
        hrService.updateCandidateStatus(candidateId, request.getStatus());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/candidates/{candidateId}/reviews")
    public ResponseEntity<ReviewDto> addReview(
            @PathVariable Long candidateId,
            @RequestBody ReviewCreateRequest request,
            @RequestHeader(value = "X-Author", required = false) String authorHeader
    ) {
        ReviewDto created = hrService.addReview(candidateId, request.getText(), authorHeader);
        return ResponseEntity.ok(created);
    }
}
