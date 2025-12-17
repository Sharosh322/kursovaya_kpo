package com.example.backend.dto;

public class AssignInterviewerRequest {
    private String interviewerEmail;

    public AssignInterviewerRequest() {}

    public AssignInterviewerRequest(String interviewerEmail) {
        this.interviewerEmail = interviewerEmail;
    }

    public String getInterviewerEmail() {
        return interviewerEmail;
    }

    public void setInterviewerEmail(String interviewerEmail) {
        this.interviewerEmail = interviewerEmail;
    }
}
