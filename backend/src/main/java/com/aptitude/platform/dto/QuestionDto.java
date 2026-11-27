package com.aptitude.platform.dto;

import java.util.List;
import java.util.UUID;

public class QuestionDto {
    private UUID id;
    private String questionText;
    private String difficulty;
    private List<OptionDto> options;

    public QuestionDto() {}

    public QuestionDto(UUID id, String questionText, String difficulty, List<OptionDto> options) {
        this.id = id;
        this.questionText = questionText;
        this.difficulty = difficulty;
        this.options = options;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public List<OptionDto> getOptions() { return options; }
    public void setOptions(List<OptionDto> options) { this.options = options; }
}
