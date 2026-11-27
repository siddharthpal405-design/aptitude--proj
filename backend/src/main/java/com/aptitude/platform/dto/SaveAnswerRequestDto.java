package com.aptitude.platform.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class SaveAnswerRequestDto {
    @NotNull(message = "Question ID is required")
    private UUID questionId;

    private String selectedOption; // null or A, B, C, D
    private boolean markedForReview = false;
    private int timeSpentSeconds = 0;

    public SaveAnswerRequestDto() {}

    public SaveAnswerRequestDto(UUID questionId, String selectedOption, boolean markedForReview, int timeSpentSeconds) {
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
