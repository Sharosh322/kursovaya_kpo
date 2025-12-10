package com.example.backend.repository;

import com.example.backend.entity.Candidate;
import com.example.backend.entity.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    List<Candidate> findByVacancy(Vacancy vacancy);
}
