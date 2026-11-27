package com.aptitude.platform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "test_sessions")
public class TestSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mock_test_id", nullable = false)
    private MockTest mockTest;

    @Column(nullable = false)
    private LocalDateTime startTime = LocalDateTime.now();

    private LocalDateTime endTime;

    @Column(nullable = false)
    private int totalQuestions = 0;

    @Column(nullable = false)
    private int attemptedCount = 0;

    @Column(nullable = false)
    private int correctCount = 0;

    @Column(nullable = false)
    private int incorrectCount = 0;

    @Column(nullable = false)
    private double totalScore = 0.0;

    @Column(nullable = false)
    private double accuracyPercentage = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestStatus status = TestStatus.IN_PROGRESS;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestSessionAnswer> answers = new ArrayList<>();

    public TestSession() {}

    public TestSession(User user, MockTest mockTest, int totalQuestions) {
        this.user = user;
        this.mockTest = mockTest;
        this.totalQuestions = totalQuestions;
        this.startTime = LocalDateTime.now();
        this.status = TestStatus.IN_PROGRESS;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public MockTest getMockTest() { return mockTest; }
    public void setMockTest(MockTest mockTest) { this.mockTest = mockTest; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getAttemptedCount() { return attemptedCount; }
    public void setAttemptedCount(int attemptedCount) { this.attemptedCount = attemptedCount; }

    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }

    public int getIncorrectCount() { return incorrectCount; }
    public void setIncorrectCount(int incorrectCount) { this.incorrectCount = incorrectCount; }

    public double getTotalScore() { return totalScore; }
    public void setTotalScore(double totalScore) { this.totalScore = totalScore; }

    public double getAccuracyPercentage() { return accuracyPercentage; }
    public void setAccuracyPercentage(double accuracyPercentage) { this.accuracyPercentage = accuracyPercentage; }

    public TestStatus getStatus() { return status; }
    public void setStatus(TestStatus status) { this.status = status; }

    public List<TestSessionAnswer> getAnswers() { return answers; }
    public void setAnswers(List<TestSessionAnswer> answers) { this.answers = answers; }
}
