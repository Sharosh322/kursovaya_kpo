package com.example.backend.dto;

public class VacancyCreateRequest {
    private String title;
    private String status;

    public VacancyCreateRequest() {
    }

    public VacancyCreateRequest(String title, String status) {
        this.title = title;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
