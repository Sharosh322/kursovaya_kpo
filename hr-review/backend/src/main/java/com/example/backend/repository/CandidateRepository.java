package com.example.backend.repository;
import com.example.backend.entity.Candidate;
import com.example.backend.entity.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    List<Candidate> findByVacancy(Vacancy vacancy);

    // интервьюер видит только назначенных ему
    List<Candidate> findByAssignedInterviewer_Email(String email);

    List<Candidate> findByVacancyAndAssignedInterviewer_Email(Vacancy vacancy, String email);

    boolean existsByIdAndAssignedInterviewer_Email(Long id, String email);

}
