package com.example.practicejava.scoring.repository;

import com.example.practicejava.scoring.Score;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScoreRepository extends JpaRepository<Score, UUID> {
    Optional<Score> findBySeasonIdAndUserId(UUID seasonId, UUID userId);
    List<Score> findBySeasonId(UUID seasonId);
}
