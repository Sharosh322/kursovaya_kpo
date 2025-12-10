package com.example.backend.repository;

import com.example.backend.entity.Candidate;
import com.example.backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByCandidate(Candidate candidate);
}
