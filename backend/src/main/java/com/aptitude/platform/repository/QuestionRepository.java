package com.aptitude.platform.repository;

import com.aptitude.platform.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findByChapterId(Long chapterId);
    Page<Question> findByChapterId(Long chapterId, Pageable pageable);
    long countByChapterId(Long chapterId);
}
