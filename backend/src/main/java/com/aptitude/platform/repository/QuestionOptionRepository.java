package com.aptitude.platform.repository;

import com.aptitude.platform.model.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, UUID> {
    List<QuestionOption> findByQuestionIdOrderByOptionKeyAsc(UUID questionId);
    Optional<QuestionOption> findByQuestionIdAndOptionKey(UUID questionId, String optionKey);
}
