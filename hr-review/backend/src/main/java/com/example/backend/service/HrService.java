package com.example.backend.service;

import com.example.backend.dto.*;
import com.example.backend.entity.Candidate;
import com.example.backend.entity.Review;
import com.example.backend.entity.Vacancy;
import com.example.backend.repository.CandidateRepository;
import com.example.backend.repository.ReviewRepository;
import com.example.backend.repository.VacancyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class HrService {

    private final VacancyRepository vacancyRepository;
    private final CandidateRepository candidateRepository;
    private final ReviewRepository reviewRepository;

    public HrService(
            VacancyRepository vacancyRepository,
            CandidateRepository candidateRepository,
            ReviewRepository reviewRepository
    ) {
        this.vacancyRepository = vacancyRepository;
        this.candidateRepository = candidateRepository;
        this.reviewRepository = reviewRepository;
    }

    public List<VacancyDto> getAllVacancies() {
        return vacancyRepository.findAll()
                .stream()
                .map(v -> new VacancyDto(v.getId(), v.getTitle(), v.getStatus()))
                .collect(Collectors.toList());
    }

    public VacancyDto createVacancy(VacancyCreateRequest request) {
        String status = Optional.ofNullable(request.getStatus()).filter(s -> !s.isBlank()).orElse("Открыта");
        Vacancy vacancy = new Vacancy(request.getTitle(), status);
        Vacancy saved = vacancyRepository.save(vacancy);
        return new VacancyDto(saved.getId(), saved.getTitle(), saved.getStatus());
    }

    public List<CandidateListItemDto> getCandidatesForVacancy(Long vacancyId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new IllegalArgumentException("Вакансия не найдена: " + vacancyId));
        return candidateRepository.findByVacancy(vacancy)
                .stream()
                .map(c -> new CandidateListItemDto(c.getId(), c.getName(), c.getStatus()))
                .collect(Collectors.toList());
    }

    public CandidateListItemDto createCandidate(Long vacancyId, CandidateCreateRequest request) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new IllegalArgumentException("Вакансия не найдена: " + vacancyId));

        String status = Optional.ofNullable(request.getStatus()).filter(s -> !s.isBlank()).orElse("Отклик получен");

        Candidate candidate = new Candidate(
                request.getName(),
                status,
                request.getEmail(),
                request.getPhone(),
                vacancy
        );
        Candidate saved = candidateRepository.save(candidate);

        return new CandidateListItemDto(saved.getId(), saved.getName(), saved.getStatus());
    }

    public CandidateDetailsDto getCandidateDetails(Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Кандидат не найден: " + candidateId));

        List<ReviewDto> reviews = reviewRepository.findByCandidate(candidate)
                .stream()
                .map(r -> new ReviewDto(r.getId(), r.getAuthor(), r.getText()))
                .collect(Collectors.toList());

        return new CandidateDetailsDto(
                candidate.getId(),
                candidate.getName(),
                candidate.getStatus(),
                candidate.getEmail(),
                candidate.getPhone(),
                "", // notes — пока пустые
                reviews
        );
    }

    public void updateCandidateStatus(Long candidateId, String status) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Кандидат не найден: " + candidateId));
        candidate.setStatus(status);
        candidateRepository.save(candidate);
    }

    public ReviewDto addReview(Long candidateId, String text, String author) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Кандидат не найден: " + candidateId));

        if (author == null || author.isBlank()) {
            author = "anonymous@example.com";
        }

        Review review = new Review(author, text, candidate);
        Review saved = reviewRepository.save(review);

        return new ReviewDto(saved.getId(), saved.getAuthor(), saved.getText());
    }
}
