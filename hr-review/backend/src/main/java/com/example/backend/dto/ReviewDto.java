package com.example.backend.dto;

public class ReviewDto {
    private Long id;
    private String author;
    private String text;

    public ReviewDto() {
    }

    public ReviewDto(Long id, String author, String text) {
        this.id = id;
        this.author = author;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setText(String text) {
        this.text = text;
    }
}
