package com.aptitude.platform.dto;

import java.util.List;
import java.util.UUID;

public class QuestionListItemDto {
    private UUID id;
    private String questionText;
    private String difficulty;
    private List<OptionDto> options;
    private String correctOption;
    private String explanation;

    public QuestionListItemDto() {}

    public QuestionListItemDto(UUID id, String questionText, String difficulty, List<OptionDto> options, String correctOption, String explanation) {
        this.id = id;
        this.questionText = questionText;
        this.difficulty = difficulty;
        this.options = options;
        this.correctOption = correctOption;
        this.explanation = explanation;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public List<OptionDto> getOptions() { return options; }
    public void setOptions(List<OptionDto> options) { this.options = options; }

    public String getCorrectOption() { return correctOption; }
    public void setCorrectOption(String correctOption) { this.correctOption = correctOption; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
