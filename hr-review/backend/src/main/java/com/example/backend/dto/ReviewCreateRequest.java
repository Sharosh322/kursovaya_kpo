package com.example.backend.dto;

public class ReviewCreateRequest {
    private String text;

    public ReviewCreateRequest() {
    }

    public ReviewCreateRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
