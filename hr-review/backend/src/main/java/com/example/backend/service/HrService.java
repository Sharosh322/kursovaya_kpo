package com.example.backend.service;

import com.example.backend.dto.*;
import com.example.backend.entity.AppUser;
import com.example.backend.entity.Candidate;
import com.example.backend.entity.Review;
import com.example.backend.entity.Vacancy;
import com.example.backend.repository.AppUserRepository;
import com.example.backend.repository.CandidateRepository;
import com.example.backend.repository.ReviewRepository;
import com.example.backend.repository.VacancyRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HrService {

    private final VacancyRepository vacancyRepository;
    private final CandidateRepository candidateRepository;
    private final ReviewRepository reviewRepository;
    private final AppUserRepository appUserRepository;

    public HrService(
            VacancyRepository vacancyRepository,
            CandidateRepository candidateRepository,
            ReviewRepository reviewRepository,
            AppUserRepository appUserRepository
    ) {
        this.vacancyRepository = vacancyRepository;
        this.candidateRepository = candidateRepository;
        this.reviewRepository = reviewRepository;
        this.appUserRepository = appUserRepository;
    }

    private boolean isAdmin(AppUser u) {
        return u != null && u.getRole() != null && u.getRole().name().equals("ADMIN");
    }

    private CandidateListItemDto toListItemDto(Candidate c) {
        Long vId = c.getVacancy() != null ? c.getVacancy().getId() : null;
        String vTitle = c.getVacancy() != null ? c.getVacancy().getTitle() : null;

        Long iId = c.getAssignedInterviewer() != null ? c.getAssignedInterviewer().getId() : null;
        String iEmail = c.getAssignedInterviewer() != null ? c.getAssignedInterviewer().getEmail() : null;
        String iName = c.getAssignedInterviewer() != null ? c.getAssignedInterviewer().getFullName() : null;

        return new CandidateListItemDto(
                c.getId(),
                c.getName(),
                c.getStatus(),
                vId,
                vTitle,
                iId,
                iEmail,
                iName
        );
    }

    // ===== Vacancies =====

    public List<VacancyDto> getAllVacancies() {
        return vacancyRepository.findAll()
                .stream()
                .map(v -> new VacancyDto(v.getId(), v.getTitle(), v.getStatus()))
                .toList();
    }

    public VacancyDto createVacancy(VacancyCreateRequest request) {
        String title = Optional.ofNullable(request.getTitle()).orElse("").trim();
        if (title.isBlank()) throw new IllegalArgumentException("Название вакансии не должно быть пустым");

        String status = Optional.ofNullable(request.getStatus()).filter(s -> !s.isBlank()).orElse("OPEN");
        Vacancy saved = vacancyRepository.save(new Vacancy(title, status));
        return new VacancyDto(saved.getId(), saved.getTitle(), saved.getStatus());
    }

    // ===== Candidates =====

    public List<CandidateListItemDto> getCandidatesForVacancy(Long vacancyId, AppUser currentUser) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new IllegalArgumentException("Вакансия не найдена: " + vacancyId));

        List<Candidate> list = isAdmin(currentUser)
                ? candidateRepository.findByVacancy(vacancy)
                : candidateRepository.findByVacancyAndAssignedInterviewer_Email(vacancy, currentUser.getEmail());

        return list.stream().map(this::toListItemDto).toList();
    }

    public CandidateListItemDto createCandidate(Long vacancyId, CandidateCreateRequest request) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new IllegalArgumentException("Вакансия не найдена: " + vacancyId));

        String name = Optional.ofNullable(request.getName()).orElse("").trim();
        if (name.isBlank()) throw new IllegalArgumentException("Имя кандидата не должно быть пустым");

        String status = Optional.ofNullable(request.getStatus()).filter(s -> !s.isBlank()).orElse("NEW");

        Candidate saved = candidateRepository.save(
                new Candidate(name, status, request.getEmail(), request.getPhone(), vacancy)
        );
        return toListItemDto(saved);
    }

    public List<CandidateListItemDto> getAllCandidatesForCurrentUser(AppUser currentUser) {
        if (isAdmin(currentUser)) {
            return candidateRepository.findAll().stream().map(this::toListItemDto).toList();
        }
        return candidateRepository.findByAssignedInterviewer_Email(currentUser.getEmail())
                .stream()
                .map(this::toListItemDto)
                .toList();
    }

    public CandidateDetailsDto getCandidateDetails(Long candidateId, AppUser currentUser) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Кандидат не найден: " + candidateId));

        if (!isAdmin(currentUser)) {
            boolean allowed = candidateRepository.existsByIdAndAssignedInterviewer_Email(candidateId, currentUser.getEmail());
            if (!allowed) throw new AccessDeniedException("Нет доступа к кандидату");
        }

        List<ReviewDto> reviews = reviewRepository.findByCandidate(candidate)
                .stream()
                .map(r -> new ReviewDto(r.getId(), r.getAuthor(), r.getText()))
                .toList();

        Long iId = candidate.getAssignedInterviewer() != null ? candidate.getAssignedInterviewer().getId() : null;
        String iEmail = candidate.getAssignedInterviewer() != null ? candidate.getAssignedInterviewer().getEmail() : null;
        String iName = candidate.getAssignedInterviewer() != null ? candidate.getAssignedInterviewer().getFullName() : null;

        return new CandidateDetailsDto(
                candidate.getId(),
                candidate.getName(),
                candidate.getStatus(),
                candidate.getEmail(),
                candidate.getPhone(),
                "",
                reviews,
                iId,
                iEmail,
                iName
        );
    }

    public void updateCandidateStatus(Long candidateId, String status) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Кандидат не найден: " + candidateId));

        String newStatus = Optional.ofNullable(status).orElse("").trim();
        if (newStatus.isBlank()) throw new IllegalArgumentException("Статус не должен быть пустым");

        candidate.setStatus(newStatus);
        candidateRepository.save(candidate);
    }

    public ReviewDto addReview(Long candidateId, String text, String author, AppUser currentUser) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Кандидат не найден: " + candidateId));

        if (!isAdmin(currentUser)) {
            boolean allowed = candidateRepository.existsByIdAndAssignedInterviewer_Email(candidateId, currentUser.getEmail());
            if (!allowed) throw new AccessDeniedException("Нет доступа: отзыв можно оставлять только по назначенным кандидатам");
        }

        String reviewText = Optional.ofNullable(text).orElse("").trim();
        if (reviewText.isBlank()) throw new IllegalArgumentException("Текст отзыва не должен быть пустым");

        String reviewAuthor = Optional.ofNullable(author).filter(a -> !a.isBlank()).orElse(currentUser.getEmail());

        Review saved = reviewRepository.save(new Review(reviewAuthor, reviewText, candidate));
        return new ReviewDto(saved.getId(), saved.getAuthor(), saved.getText());
    }

    

    // ===== Assign interviewer (ADMIN) =====

    public void assignInterviewer(Long candidateId, String interviewerEmail) {
        String email = Optional.ofNullable(interviewerEmail).orElse("").trim();
        if (email.isBlank()) throw new IllegalArgumentException("interviewerEmail не должен быть пустым");

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Кандидат не найден: " + candidateId));

        AppUser interviewer = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + email));

        candidate.setAssignedInterviewer(interviewer);
        candidateRepository.save(candidate);
    }
}
