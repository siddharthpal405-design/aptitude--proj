package com.aptitude.platform.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StartTestResponseDto {
    private UUID sessionId;
    private UUID mockTestId;
    private String title;
    private int durationMinutes;
    private int totalMarks;
    private double negativeMarkingRate;
    private LocalDateTime startTime;
    private List<QuestionDto> questions;
    private Map<UUID, SavedAnswerStateDto> savedAnswers;

    public StartTestResponseDto() {}

    public StartTestResponseDto(UUID sessionId, UUID mockTestId, String title, int durationMinutes, int totalMarks, double negativeMarkingRate, LocalDateTime startTime, List<QuestionDto> questions, Map<UUID, SavedAnswerStateDto> savedAnswers) {
        this.sessionId = sessionId;
        this.mockTestId = mockTestId;
        this.title = title;
        this.durationMinutes = durationMinutes;
        this.totalMarks = totalMarks;
        this.negativeMarkingRate = negativeMarkingRate;
        this.startTime = startTime;
        this.questions = questions;
        this.savedAnswers = savedAnswers;
    }

    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }

    public UUID getMockTestId() { return mockTestId; }
    public void setMockTestId(UUID mockTestId) { this.mockTestId = mockTestId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public int getTotalMarks() { return totalMarks; }
    public void setTotalMarks(int totalMarks) { this.totalMarks = totalMarks; }

    public double getNegativeMarkingRate() { return negativeMarkingRate; }
    public void setNegativeMarkingRate(double negativeMarkingRate) { this.negativeMarkingRate = negativeMarkingRate; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public List<QuestionDto> getQuestions() { return questions; }
    public void setQuestions(List<QuestionDto> questions) { this.questions = questions; }

    public Map<UUID, SavedAnswerStateDto> getSavedAnswers() { return savedAnswers; }
    public void setSavedAnswers(Map<UUID, SavedAnswerStateDto> savedAnswers) { this.savedAnswers = savedAnswers; }
}
