package com.example.backend.dto;

public class CandidateListItemDto {
    private Long id;
    private String name;
    private String status;
    private Long vacancyId;
    private String vacancyTitle;

    private Long assignedInterviewerId;
    private String assignedInterviewerEmail;
    private String assignedInterviewerFullName;

    public CandidateListItemDto() {}

    public CandidateListItemDto(
            Long id,
            String name,
            String status,
            Long vacancyId,
            String vacancyTitle,
            Long assignedInterviewerId,
            String assignedInterviewerEmail,
            String assignedInterviewerFullName
    ) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.vacancyId = vacancyId;
        this.vacancyTitle = vacancyTitle;
        this.assignedInterviewerId = assignedInterviewerId;
        this.assignedInterviewerEmail = assignedInterviewerEmail;
        this.assignedInterviewerFullName = assignedInterviewerFullName;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public Long getVacancyId() { return vacancyId; }
    public String getVacancyTitle() { return vacancyTitle; }

    public Long getAssignedInterviewerId() { return assignedInterviewerId; }
    public String getAssignedInterviewerEmail() { return assignedInterviewerEmail; }
    public String getAssignedInterviewerFullName() { return assignedInterviewerFullName; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setStatus(String status) { this.status = status; }
    public void setVacancyId(Long vacancyId) { this.vacancyId = vacancyId; }
    public void setVacancyTitle(String vacancyTitle) { this.vacancyTitle = vacancyTitle; }

    public void setAssignedInterviewerId(Long assignedInterviewerId) { this.assignedInterviewerId = assignedInterviewerId; }
    public void setAssignedInterviewerEmail(String assignedInterviewerEmail) { this.assignedInterviewerEmail = assignedInterviewerEmail; }
    public void setAssignedInterviewerFullName(String assignedInterviewerFullName) { this.assignedInterviewerFullName = assignedInterviewerFullName; }
}
