package com.example.backend.service;

import com.example.backend.dto.*;
import com.example.backend.entity.*;
import com.example.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HrServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private HrService hrService;

    private AppUser adminUser;
    private AppUser regularUser;
    private Vacancy vacancy;

    @BeforeEach
    void setUp() {
        adminUser = new AppUser();
        adminUser.setId(1L);
        adminUser.setEmail("admin@example.com");
        adminUser.setRole(Role.ADMIN);

        regularUser = new AppUser();
        regularUser.setId(2L);
        regularUser.setEmail("user@example.com");
        regularUser.setRole(Role.USER);

        vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setTitle("Java Developer");
        vacancy.setStatus("OPEN");
    }

    @Test
    void createVacancy_ValidRequest_ReturnsVacancyDto() {
        // Arrange
        VacancyCreateRequest request = new VacancyCreateRequest("New Position", "OPEN");
        Vacancy savedVacancy = new Vacancy("New Position", "OPEN");
        savedVacancy.setId(1L);
        
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(savedVacancy);

        // Act
        VacancyDto result = hrService.createVacancy(request);

        // Assert
        assertNotNull(result);
        assertEquals("New Position", result.getTitle());
        assertEquals("OPEN", result.getStatus());
        verify(vacancyRepository).save(any(Vacancy.class));
    }

    @Test
    void createVacancy_EmptyTitle_ThrowsException() {
        // Arrange
        VacancyCreateRequest request = new VacancyCreateRequest("", "OPEN");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> hrService.createVacancy(request)
        );
        assertEquals("Название вакансии не должно быть пустым", exception.getMessage());
    }

    @Test
    void getCandidatesForVacancy_AdminUser_ReturnsAllCandidates() {
        // Arrange
        Candidate candidate = new Candidate("John Doe", "NEW", "john@example.com", "123456", vacancy);
        candidate.setId(1L);
        
        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));
        when(candidateRepository.findByVacancy(vacancy)).thenReturn(List.of(candidate));

        // Act
        List<CandidateListItemDto> result = hrService.getCandidatesForVacancy(1L, adminUser);

        // Assert
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        verify(candidateRepository).findByVacancy(vacancy);
        verify(candidateRepository, never()).findByVacancyAndAssignedInterviewer_Email(any(), any());
    }

    @Test
    void getCandidateDetails_RegularUserWithAccess_ReturnsDetails() {
        // Arrange
        Candidate candidate = new Candidate("John Doe", "NEW", "john@example.com", "123456", vacancy);
        candidate.setId(1L);
        candidate.setAssignedInterviewer(regularUser);
        
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(candidateRepository.existsByIdAndAssignedInterviewer_Email(1L, "user@example.com"))
            .thenReturn(true);
        when(reviewRepository.findByCandidate(candidate)).thenReturn(List.of());

        // Act
        CandidateDetailsDto result = hrService.getCandidateDetails(1L, regularUser);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(candidateRepository).existsByIdAndAssignedInterviewer_Email(1L, "user@example.com");
    }

    @Test
    void getCandidateDetails_RegularUserWithoutAccess_ThrowsAccessDenied() {
        // Arrange
        Candidate candidate = new Candidate();
        candidate.setId(1L);
        
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(candidateRepository.existsByIdAndAssignedInterviewer_Email(1L, "user@example.com"))
            .thenReturn(false);

        // Act & Assert
        assertThrows(
            AccessDeniedException.class,
            () -> hrService.getCandidateDetails(1L, regularUser)
        );
    }

    @Test
    void addReview_AdminUser_ReturnsReviewDto() {
        // Arrange
        Candidate candidate = new Candidate();
        candidate.setId(1L);
        
        Review review = new Review("admin@example.com", "Great candidate", candidate);
        review.setId(1L);
        
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // Act
        ReviewDto result = hrService.addReview(1L, "Great candidate", null, adminUser);

        // Assert
        assertNotNull(result);
        assertEquals("admin@example.com", result.getAuthor());
        assertEquals("Great candidate", result.getText());
    }

    @Test
    void assignInterviewer_ValidRequest_UpdatesCandidate() {
        // Arrange
        Candidate candidate = new Candidate();
        candidate.setId(1L);
        candidate.setAssignedInterviewer(null);
        
        AppUser interviewer = new AppUser();
        interviewer.setId(2L);
        interviewer.setEmail("interviewer@example.com");

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(appUserRepository.findByEmail("interviewer@example.com")).thenReturn(Optional.of(interviewer));
        when(candidateRepository.save(candidate)).thenReturn(candidate);

        // Act
        hrService.assignInterviewer(1L, "interviewer@example.com");

        // Assert
        assertEquals(interviewer, candidate.getAssignedInterviewer());
        verify(candidateRepository).save(candidate);
    }
}
