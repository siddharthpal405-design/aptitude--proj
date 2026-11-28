package com.aptitude.platform.repository;

import com.aptitude.platform.model.Difficulty;
import com.aptitude.platform.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findByChapterId(Long chapterId);
    Page<Question> findByChapterId(Long chapterId, Pageable pageable);
    long countByChapterId(Long chapterId);
    long countByChapterIdAndDifficulty(Long chapterId, Difficulty difficulty);

    @Query("SELECT q FROM Question q WHERE q.chapter.id = :chapterId " +
           "AND (:difficulty IS NULL OR q.difficulty = :difficulty) " +
           "AND (:search IS NULL OR LOWER(q.questionText) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY q.createdAt ASC, q.id ASC")
    Page<Question> searchByChapter(
            @Param("chapterId") Long chapterId,
            @Param("difficulty") Difficulty difficulty,
            @Param("search") String search,
            Pageable pageable
    );
}
