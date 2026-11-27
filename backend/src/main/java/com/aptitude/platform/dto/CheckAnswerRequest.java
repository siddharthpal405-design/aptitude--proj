package com.aptitude.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CheckAnswerRequest {
    @NotNull(message = "Question ID is required")
    private UUID questionId;

    @NotBlank(message = "Selected option is required")
    private String selectedOption; // A, B, C, D

    public CheckAnswerRequest() {}

    public CheckAnswerRequest(UUID questionId, String selectedOption) {
        this.questionId = questionId;
        this.selectedOption = selectedOption;
    }

    public UUID getQuestionId() { return questionId; }
    public void setQuestionId(UUID questionId) { this.questionId = questionId; }

    public String getSelectedOption() { return selectedOption; }
    public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }
}
