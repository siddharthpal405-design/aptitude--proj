package com.aptitude.platform.dto;

import java.util.UUID;

public class CheckAnswerResponse {
    private UUID questionId;
    private String selectedOption;
    private String correctOption;
    private boolean isCorrect;
    private String explanation;
    private int currentStreak;

    public CheckAnswerResponse() {}

    public CheckAnswerResponse(UUID questionId, String selectedOption, String correctOption, boolean isCorrect, String explanation, int currentStreak) {
        this.questionId = questionId;
        this.selectedOption = selectedOption;
        this.correctOption = correctOption;
        this.isCorrect = isCorrect;
        this.explanation = explanation;
        this.currentStreak = currentStreak;
    }

    public UUID getQuestionId() { return questionId; }
    public void setQuestionId(UUID questionId) { this.questionId = questionId; }

    public String getSelectedOption() { return selectedOption; }
    public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }

    public String getCorrectOption() { return correctOption; }
    public void setCorrectOption(String correctOption) { this.correctOption = correctOption; }

    public boolean isCorrect() { return isCorrect; }
    public boolean getIsCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
}
