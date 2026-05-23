package com.example.practicejava.scoring;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScoreBreakdownRepository extends JpaRepository<ScoreBreakdown, UUID> {
    List<ScoreBreakdown> findByScoreId(UUID scoreId);
}
