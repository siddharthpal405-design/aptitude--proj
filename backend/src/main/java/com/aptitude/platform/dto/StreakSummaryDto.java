package com.aptitude.platform.dto;

import java.time.LocalDate;

public class StreakSummaryDto {
    private int currentStreak;
    private int longestStreak;
    private LocalDate lastActivityDate;
    private int todayQuestionsSolved;

    public StreakSummaryDto() {}

    public StreakSummaryDto(int currentStreak, int longestStreak, LocalDate lastActivityDate, int todayQuestionsSolved) {
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.lastActivityDate = lastActivityDate;
        this.todayQuestionsSolved = todayQuestionsSolved;
    }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }

    public LocalDate getLastActivityDate() { return lastActivityDate; }
    public void setLastActivityDate(LocalDate lastActivityDate) { this.lastActivityDate = lastActivityDate; }

    public int getTodayQuestionsSolved() { return todayQuestionsSolved; }
    public void setTodayQuestionsSolved(int todayQuestionsSolved) { this.todayQuestionsSolved = todayQuestionsSolved; }
}
