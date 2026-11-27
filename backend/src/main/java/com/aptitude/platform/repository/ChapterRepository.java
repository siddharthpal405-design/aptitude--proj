package com.aptitude.platform.repository;

import com.aptitude.platform.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByCategoryIdOrderByDisplayOrderAsc(Long categoryId);
    Optional<Chapter> findBySlug(String slug);
}
