package com.aptitude.platform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "question_options")
public class QuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private Question question;

    @Column(nullable = false, length = 1)
    private String optionKey; // A, B, C, D

    @Column(columnDefinition = "TEXT", nullable = false)
    private String optionText;

    @Column(nullable = false)
    private boolean isCorrect = false;

    public QuestionOption() {}

    public QuestionOption(String optionKey, String optionText, boolean isCorrect) {
        this.optionKey = optionKey;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public String getOptionKey() { return optionKey; }
    public void setOptionKey(String optionKey) { this.optionKey = optionKey; }

    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
}
