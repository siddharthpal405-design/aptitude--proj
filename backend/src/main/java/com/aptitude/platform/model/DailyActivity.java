package com.aptitude.platform.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "daily_activity")
public class DailyActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate activityDate;

    @Column(nullable = false)
    private int questionsSolved = 0;

    public DailyActivity() {}

    public DailyActivity(User user, LocalDate activityDate, int questionsSolved) {
        this.user = user;
        this.activityDate = activityDate;
        this.questionsSolved = questionsSolved;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDate getActivityDate() { return activityDate; }
    public void setActivityDate(LocalDate activityDate) { this.activityDate = activityDate; }

    public int getQuestionsSolved() { return questionsSolved; }
    public void setQuestionsSolved(int questionsSolved) { this.questionsSolved = questionsSolved; }
}
