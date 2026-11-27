package com.aptitude.platform.dto;

import java.util.UUID;

public class MockTestSummaryDto {
    private UUID id;
    private String title;
    private String description;
    private int durationMinutes;
    private int totalQuestions;
    private int totalMarks;
    private double negativeMarkingRate;
    private String difficulty;

    public MockTestSummaryDto() {}

    public MockTestSummaryDto(UUID id, String title, String description, int durationMinutes, int totalQuestions, int totalMarks, double negativeMarkingRate, String difficulty) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.totalQuestions = totalQuestions;
        this.totalMarks = totalMarks;
        this.negativeMarkingRate = negativeMarkingRate;
        this.difficulty = difficulty;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getTotalMarks() { return totalMarks; }
    public void setTotalMarks(int totalMarks) { this.totalMarks = totalMarks; }

    public double getNegativeMarkingRate() { return negativeMarkingRate; }
    public void setNegativeMarkingRate(double negativeMarkingRate) { this.negativeMarkingRate = negativeMarkingRate; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}
