package com.example.practicejava.scoring.repository;

import com.example.practicejava.scoring.ScoreBreakdown;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScoreBreakdownRepository extends JpaRepository<ScoreBreakdown, UUID> {
    List<ScoreBreakdown> findByScoreId(UUID scoreId);
    List<ScoreBreakdown> findByScoreSeasonIdAndScoreUserId(UUID seasonId, UUID userId);
}
