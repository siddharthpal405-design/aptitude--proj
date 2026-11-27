package com.aptitude.platform.repository;

import com.aptitude.platform.model.MockTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MockTestRepository extends JpaRepository<MockTest, UUID> {
    List<MockTest> findByIsActiveTrueOrderByCreatedAtDesc();
}
