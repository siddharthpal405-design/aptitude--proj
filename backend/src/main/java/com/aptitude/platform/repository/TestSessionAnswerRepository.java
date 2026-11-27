package com.aptitude.platform.repository;

import com.aptitude.platform.model.TestSessionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TestSessionAnswerRepository extends JpaRepository<TestSessionAnswer, UUID> {
    List<TestSessionAnswer> findBySessionId(UUID sessionId);
    Optional<TestSessionAnswer> findBySessionIdAndQuestionId(UUID sessionId, UUID questionId);
}
