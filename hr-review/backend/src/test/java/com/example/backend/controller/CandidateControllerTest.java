package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.entity.AppUser;
import com.example.backend.entity.Role;
import com.example.backend.service.HrService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateControllerTest {

    @Mock
    private HrService hrService;

    @InjectMocks
    private CandidateController candidateController;

    private AppUser adminUser;
    private AppUser regularUser;

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
    }

    @Test
    void getCandidatesForVacancy_ReturnsList() {
        // Arrange
        List<CandidateListItemDto> candidates = List.of(
            new CandidateListItemDto(1L, "John Doe", "NEW", 1L, "Java Dev", null, null, null),
            new CandidateListItemDto(2L, "Jane Smith", "REVIEW", 1L, "Java Dev", null, null, null)
        );
        
        when(hrService.getCandidatesForVacancy(1L, regularUser)).thenReturn(candidates);

        // Act
        List<CandidateListItemDto> result = candidateController.getCandidates(1L, regularUser);

        // Assert
        assertEquals(2, result.size());
        verify(hrService).getCandidatesForVacancy(1L, regularUser);
    }

    @Test
    void createCandidate_AdminUser_ReturnsCreated() {
        // Arrange
        CandidateCreateRequest request = new CandidateCreateRequest();
        request.setName("New Candidate");
        request.setEmail("new@example.com");
        request.setPhone("123456");
        request.setStatus("NEW");

        CandidateListItemDto createdDto = new CandidateListItemDto(
            1L, "New Candidate", "NEW", 1L, "Java Dev", null, null, null
        );
        
        when(hrService.createCandidate(1L, request)).thenReturn(createdDto);

        // Act
        ResponseEntity<CandidateListItemDto> response = candidateController.createCandidate(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("New Candidate", response.getBody().getName());
    }

    @Test
    void getCandidate_ValidId_ReturnsDetails() {
        // Arrange
        CandidateDetailsDto details = new CandidateDetailsDto(
            1L, "John Doe", "NEW", "john@example.com", "123456", 
            "Notes", List.of(), null, null, null
        );
        
        when(hrService.getCandidateDetails(1L, regularUser)).thenReturn(details);

        // Act
        ResponseEntity<CandidateDetailsDto> response = candidateController.getCandidate(1L, regularUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getName());
    }

    @Test
    void updateStatus_AdminUser_ReturnsNoContent() {
        // Arrange
        StatusUpdateRequest request = new StatusUpdateRequest("APPROVED");
        doNothing().when(hrService).updateCandidateStatus(1L, "APPROVED");

        // Act
        ResponseEntity<Void> response = candidateController.updateStatus(1L, request);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(hrService).updateCandidateStatus(1L, "APPROVED");
    }

    @Test
    void addReview_ValidRequest_ReturnsReview() {
        // Arrange
        ReviewCreateRequest request = new ReviewCreateRequest("Good candidate");
        ReviewDto reviewDto = new ReviewDto(1L, "user@example.com", "Good candidate");
        
        when(hrService.addReview(eq(1L), eq("Good candidate"), any(), eq(regularUser)))
            .thenReturn(reviewDto);

        // Act
        ResponseEntity<ReviewDto> response = candidateController.addReview(1L, request, null, regularUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Good candidate", response.getBody().getText());
    }

    @Test
    void getAllCandidates_ReturnsFilteredList() {
        // Arrange
        List<CandidateListItemDto> candidates = List.of(
            new CandidateListItemDto(1L, "John Doe", "NEW", 1L, "Java Dev", null, null, null)
        );
        
        when(hrService.getAllCandidatesForCurrentUser(regularUser)).thenReturn(candidates);

        // Act
        List<CandidateListItemDto> result = candidateController.getAllCandidates(regularUser);

        // Assert
        assertEquals(1, result.size());
        verify(hrService).getAllCandidatesForCurrentUser(regularUser);
    }

    @Test
    void assignInterviewer_AdminUser_ReturnsNoContent() {
        // Arrange
        AssignInterviewerRequest request = new AssignInterviewerRequest("interviewer@example.com");
        doNothing().when(hrService).assignInterviewer(1L, "interviewer@example.com");

        // Act
        ResponseEntity<Void> response = candidateController.assign(1L, request);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(hrService).assignInterviewer(1L, "interviewer@example.com");
    }

    @Test
    void getMyCandidates_ReturnsUserCandidates() {
        // Arrange
        List<CandidateListItemDto> candidates = List.of(
            new CandidateListItemDto(1L, "John Doe", "NEW", 1L, "Java Dev", 2L, "user@example.com", "Interviewer Name")
        );
        
        when(hrService.getAllCandidatesForCurrentUser(regularUser)).thenReturn(candidates);

        // Act
        ResponseEntity<List<CandidateListItemDto>> response = candidateController.getMyCandidates(regularUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }
}