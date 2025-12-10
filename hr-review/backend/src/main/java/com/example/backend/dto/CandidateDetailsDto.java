package com.example.backend.dto;

import java.util.List;

public class CandidateDetailsDto {
    private Long id;
    private String name;
    private String status;
    private String email;
    private String phone;
    private String notes;
    private List<ReviewDto> reviews;

    public CandidateDetailsDto() {
    }

    public CandidateDetailsDto(
            Long id,
            String name,
            String status,
            String email,
            String phone,
            String notes,
            List<ReviewDto> reviews
    ) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.email = email;
        this.phone = phone;
        this.notes = notes;
        this.reviews = reviews;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getNotes() {
        return notes;
    }

    public List<ReviewDto> getReviews() {
        return reviews;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setReviews(List<ReviewDto> reviews) {
        this.reviews = reviews;
    }
}
