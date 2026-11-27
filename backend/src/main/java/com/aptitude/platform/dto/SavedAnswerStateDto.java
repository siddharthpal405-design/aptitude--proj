package com.aptitude.platform.dto;

import java.util.UUID;

public class SavedAnswerStateDto {
    private UUID questionId;
    private String selectedOption;
    private boolean markedForReview;
    private int timeSpentSeconds;

    public SavedAnswerStateDto() {}

    public SavedAnswerStateDto(UUID questionId, String selectedOption, boolean markedForReview, int timeSpentSeconds) {
        this.questionId = questionId;
        this.selectedOption = selectedOption;
        this.markedForReview = markedForReview;
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public UUID getQuestionId() { return questionId; }
    public void setQuestionId(UUID questionId) { this.questionId = questionId; }

    public String getSelectedOption() { return selectedOption; }
    public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }

    public boolean isMarkedForReview() { return markedForReview; }
    public void setMarkedForReview(boolean markedForReview) { this.markedForReview = markedForReview; }

    public int getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(int timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }
}
