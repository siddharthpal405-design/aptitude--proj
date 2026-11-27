package com.aptitude.platform.repository;

import com.aptitude.platform.model.DailyActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailyActivityRepository extends JpaRepository<DailyActivity, UUID> {
    Optional<DailyActivity> findByUserIdAndActivityDate(UUID userId, LocalDate activityDate);
    List<DailyActivity> findByUserIdAndActivityDateBetweenOrderByActivityDateAsc(UUID userId, LocalDate startDate, LocalDate endDate);
}
