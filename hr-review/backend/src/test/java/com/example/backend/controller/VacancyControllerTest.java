package com.example.backend.controller;

import com.example.backend.dto.VacancyCreateRequest;
import com.example.backend.dto.VacancyDto;
import com.example.backend.service.HrService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyControllerTest {

    @Mock
    private HrService hrService;

    @InjectMocks
    private VacancyController vacancyController;

    @Test
    void getAll_ReturnsVacancies() {
        // Arrange
        List<VacancyDto> vacancies = List.of(
            new VacancyDto(1L, "Java Developer", "OPEN"),
            new VacancyDto(2L, "Frontend Developer", "CLOSED")
        );
        
        when(hrService.getAllVacancies()).thenReturn(vacancies);

        // Act
        List<VacancyDto> result = vacancyController.getAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Java Developer", result.get(0).getTitle());
        verify(hrService).getAllVacancies();
    }

    @Test
    void create_ValidRequest_ReturnsCreatedVacancy() {
        // Arrange
        VacancyCreateRequest request = new VacancyCreateRequest("New Position", "OPEN");
        VacancyDto createdDto = new VacancyDto(1L, "New Position", "OPEN");
        
        when(hrService.createVacancy(request)).thenReturn(createdDto);

        // Act
        ResponseEntity<VacancyDto> response = vacancyController.create(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("New Position", response.getBody().getTitle());
        verify(hrService).createVacancy(request);
    }
}