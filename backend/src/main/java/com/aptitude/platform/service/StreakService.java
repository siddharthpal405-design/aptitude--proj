package com.aptitude.platform.service;

import com.aptitude.platform.dto.HeatmapDayDto;
import com.aptitude.platform.dto.StreakSummaryDto;
import com.aptitude.platform.model.DailyActivity;
import com.aptitude.platform.model.User;
import com.aptitude.platform.model.UserStreak;
import com.aptitude.platform.repository.DailyActivityRepository;
import com.aptitude.platform.repository.UserStreakRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StreakService {

    private final UserStreakRepository userStreakRepository;
    private final DailyActivityRepository dailyActivityRepository;

    public StreakService(UserStreakRepository userStreakRepository, DailyActivityRepository dailyActivityRepository) {
        this.userStreakRepository = userStreakRepository;
        this.dailyActivityRepository = dailyActivityRepository;
    }

    @Transactional
    public UserStreak recordActivityAndCalculateStreak(User user) {
        return recordActivityWithCount(user, 1);
    }

    @Transactional
    public UserStreak recordMockTestActivity(User user, int count) {
        return recordActivityWithCount(user, count);
    }

    private UserStreak recordActivityWithCount(User user, int count) {
        LocalDate today = LocalDate.now();

        // 1. Update Daily Activity
        DailyActivity dailyActivity = dailyActivityRepository.findByUserIdAndActivityDate(user.getId(), today)
                .orElseGet(() -> new DailyActivity(user, today, 0));
        dailyActivity.setQuestionsSolved(dailyActivity.getQuestionsSolved() + count);
        dailyActivityRepository.save(dailyActivity);

        // 2. Fetch or create UserStreak
        UserStreak userStreak = userStreakRepository.findByUserId(user.getId())
                .orElseGet(() -> new UserStreak(user));

        LocalDate lastDate = userStreak.getLastActivityDate();

        if (lastDate == null) {
            userStreak.setCurrentStreak(1);
        } else if (lastDate.equals(today.minusDays(1))) {
            userStreak.setCurrentStreak(userStreak.getCurrentStreak() + 1);
        } else if (lastDate.isBefore(today.minusDays(1))) {
            userStreak.setCurrentStreak(1);
        } // if lastDate == today, keep current streak

        userStreak.setLongestStreak(Math.max(userStreak.getLongestStreak(), userStreak.getCurrentStreak()));
        userStreak.setLastActivityDate(today);
        userStreak.setUpdatedAt(LocalDateTime.now());

        return userStreakRepository.save(userStreak);
    }

    public StreakSummaryDto getStreakSummary(User user) {
        LocalDate today = LocalDate.now();
        UserStreak streak = userStreakRepository.findByUserId(user.getId())
                .orElseGet(() -> new UserStreak(user));

        int effectiveStreak = streak.getCurrentStreak();
        if (streak.getLastActivityDate() != null &&
                !streak.getLastActivityDate().equals(today) &&
                !streak.getLastActivityDate().equals(today.minusDays(1))) {
            effectiveStreak = 0;
        }

        int todayQuestions = dailyActivityRepository.findByUserIdAndActivityDate(user.getId(), today)
                .map(DailyActivity::getQuestionsSolved)
                .orElse(0);

        return new StreakSummaryDto(
                effectiveStreak,
                streak.getLongestStreak(),
                streak.getLastActivityDate(),
                todayQuestions
        );
    }

    public List<HeatmapDayDto> getYearlyActivity(User user) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(364);

        List<DailyActivity> activities = dailyActivityRepository.findByUserIdAndActivityDateBetweenOrderByActivityDateAsc(
                user.getId(), startDate, today
        );

        Map<LocalDate, Integer> activityMap = activities.stream()
                .collect(Collectors.toMap(DailyActivity::getActivityDate, DailyActivity::getQuestionsSolved, (v1, v2) -> v1));

        List<HeatmapDayDto> heatmap = new ArrayList<>(365);
        for (int i = 0; i < 365; i++) {
            LocalDate date = startDate.plusDays(i);
            int count = activityMap.getOrDefault(date, 0);
            int level = 0;
            if (count > 0 && count < 5) level = 1;
            else if (count >= 5 && count < 10) level = 2;
            else if (count >= 10 && count < 20) level = 3;
            else if (count >= 20) level = 4;

            heatmap.add(new HeatmapDayDto(date, count, level));
        }

        return heatmap;
    }
}
