package com.example.backend.repository;

import com.example.backend.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateRepositoryTest {

    @Mock
    private CandidateRepository candidateRepository;

    @Test
    void findByVacancy_ReturnsCandidates() {
        // Arrange
        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        
        Candidate candidate = new Candidate();
        candidate.setId(1L);
        candidate.setName("John Doe");
        candidate.setVacancy(vacancy);

        when(candidateRepository.findByVacancy(vacancy)).thenReturn(List.of(candidate));

        // Act
        List<Candidate> result = candidateRepository.findByVacancy(vacancy);

        // Assert
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        verify(candidateRepository).findByVacancy(vacancy);
    }

    @Test
    void findByAssignedInterviewer_Email_ReturnsCandidates() {
        // Arrange
        Candidate candidate = new Candidate();
        candidate.setId(1L);
        candidate.setName("John Doe");

        when(candidateRepository.findByAssignedInterviewer_Email("interviewer@example.com"))
            .thenReturn(List.of(candidate));

        // Act
        List<Candidate> result = candidateRepository.findByAssignedInterviewer_Email("interviewer@example.com");

        // Assert
        assertEquals(1, result.size());
        verify(candidateRepository).findByAssignedInterviewer_Email("interviewer@example.com");
    }

    @Test
    void existsByIdAndAssignedInterviewer_Email_ValidAssignment_ReturnsTrue() {
        // Arrange
        when(candidateRepository.existsByIdAndAssignedInterviewer_Email(1L, "interviewer@example.com"))
            .thenReturn(true);

        // Act
        boolean exists = candidateRepository.existsByIdAndAssignedInterviewer_Email(1L, "interviewer@example.com");

        // Assert
        assertTrue(exists);
        verify(candidateRepository).existsByIdAndAssignedInterviewer_Email(1L, "interviewer@example.com");
    }
}