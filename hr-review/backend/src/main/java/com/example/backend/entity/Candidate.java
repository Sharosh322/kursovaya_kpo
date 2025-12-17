package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String status;
    private String email;
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacancy_id")
    private Vacancy vacancy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_interviewer_id")
    private AppUser assignedInterviewer;

    public Candidate() {}

    public Candidate(String name, String status, String email, String phone, Vacancy vacancy) {
        this.name = name;
        this.status = status;
        this.email = email;
        this.phone = phone;
        this.vacancy = vacancy;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Vacancy getVacancy() { return vacancy; }
    public AppUser getAssignedInterviewer() { return assignedInterviewer; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setStatus(String status) { this.status = status; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setVacancy(Vacancy vacancy) { this.vacancy = vacancy; }
    public void setAssignedInterviewer(AppUser assignedInterviewer) { this.assignedInterviewer = assignedInterviewer; }
}
