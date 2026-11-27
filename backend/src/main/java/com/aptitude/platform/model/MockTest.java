package com.aptitude.platform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "mock_tests")
public class MockTest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1500)
    private String description;

    @Column(nullable = false)
    private int durationMinutes = 30;

    @Column(nullable = false)
    private int totalQuestions = 15;

    @Column(nullable = false)
    private int totalMarks = 15;

    @Column(nullable = false)
    private double negativeMarkingRate = 0.25;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty = Difficulty.MEDIUM;

    @Column(nullable = false)
    private boolean isActive = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "mock_test_questions",
            joinColumns = @JoinColumn(name = "mock_test_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questions = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public MockTest() {}

    public MockTest(String title, String description, int durationMinutes, int totalMarks, double negativeMarkingRate, Difficulty difficulty) {
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.totalMarks = totalMarks;
        this.negativeMarkingRate = negativeMarkingRate;
        this.difficulty = difficulty;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
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

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
        this.totalQuestions = questions != null ? questions.size() : 0;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
