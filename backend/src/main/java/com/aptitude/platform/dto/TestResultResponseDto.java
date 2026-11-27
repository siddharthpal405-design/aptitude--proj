package com.aptitude.platform.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TestResultResponseDto {
    private UUID sessionId;
    private String testTitle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int totalQuestions;
    private int attemptedCount;
    private int correctCount;
    private int incorrectCount;
    private int skippedCount;
    private double totalScore;
    private int maxMarks;
    private double accuracyPercentage;
    private double percentileEstimate;
    private int totalTimeTakenSeconds;
    private List<QuestionReviewDto> reviewList;

    public TestResultResponseDto() {}

    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }

    public String getTestTitle() { return testTitle; }
    public void setTestTitle(String testTitle) { this.testTitle = testTitle; }

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

    public int getSkippedCount() { return skippedCount; }
    public void setSkippedCount(int skippedCount) { this.skippedCount = skippedCount; }

    public double getTotalScore() { return totalScore; }
    public void setTotalScore(double totalScore) { this.totalScore = totalScore; }

    public int getMaxMarks() { return maxMarks; }
    public void setMaxMarks(int maxMarks) { this.maxMarks = maxMarks; }

    public double getAccuracyPercentage() { return accuracyPercentage; }
    public void setAccuracyPercentage(double accuracyPercentage) { this.accuracyPercentage = accuracyPercentage; }

    public double getPercentileEstimate() { return percentileEstimate; }
    public void setPercentileEstimate(double percentileEstimate) { this.percentileEstimate = percentileEstimate; }

    public int getTotalTimeTakenSeconds() { return totalTimeTakenSeconds; }
    public void setTotalTimeTakenSeconds(int totalTimeTakenSeconds) { this.totalTimeTakenSeconds = totalTimeTakenSeconds; }

    public List<QuestionReviewDto> getReviewList() { return reviewList; }
    public void setReviewList(List<QuestionReviewDto> reviewList) { this.reviewList = reviewList; }
}
