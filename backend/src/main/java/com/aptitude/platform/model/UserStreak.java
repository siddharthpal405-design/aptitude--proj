package com.aptitude.platform.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_streaks")
public class UserStreak {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private int currentStreak = 0;

    @Column(nullable = false)
    private int longestStreak = 0;

    private LocalDate lastActivityDate;

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public UserStreak() {}

    public UserStreak(User user) {
        this.user = user;
        this.currentStreak = 0;
        this.longestStreak = 0;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }

    public LocalDate getLastActivityDate() { return lastActivityDate; }
    public void setLastActivityDate(LocalDate lastActivityDate) { this.lastActivityDate = lastActivityDate; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
