package com.aptitude.platform.dto;

import java.util.List;
import java.util.UUID;

public class QuestionReviewDto {
    private UUID questionId;
    private String questionText;
    private String difficulty;
    private String selectedOption;
    private String correctOption;
    private boolean isCorrect;
    private boolean isAttempted;
    private String explanation;
    private List<OptionDto> options;

    public QuestionReviewDto() {}

    public QuestionReviewDto(UUID questionId, String questionText, String difficulty, String selectedOption, String correctOption, boolean isCorrect, boolean isAttempted, String explanation, List<OptionDto> options) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.difficulty = difficulty;
        this.selectedOption = selectedOption;
        this.correctOption = correctOption;
        this.isCorrect = isCorrect;
        this.isAttempted = isAttempted;
        this.explanation = explanation;
        this.options = options;
    }

    public UUID getQuestionId() { return questionId; }
    public void setQuestionId(UUID questionId) { this.questionId = questionId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getSelectedOption() { return selectedOption; }
    public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }

    public String getCorrectOption() { return correctOption; }
    public void setCorrectOption(String correctOption) { this.correctOption = correctOption; }

    public boolean isCorrect() { return isCorrect; }
    public boolean getIsCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }

    public boolean isAttempted() { return isAttempted; }
    public boolean getIsAttempted() { return isAttempted; }
    public void setAttempted(boolean attempted) { isAttempted = attempted; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public List<OptionDto> getOptions() { return options; }
    public void setOptions(List<OptionDto> options) { this.options = options; }
}
