package com.aptitude.platform.repository;

import com.aptitude.platform.model.TestSession;
import com.aptitude.platform.model.TestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TestSessionRepository extends JpaRepository<TestSession, UUID> {
    List<TestSession> findByUserIdOrderByStartTimeDesc(UUID userId);
    Optional<TestSession> findByIdAndUserId(UUID id, UUID userId);
    List<TestSession> findByUserIdAndStatus(UUID userId, TestStatus status);
}
