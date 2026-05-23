package com.example.practicejava.scoring;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ScoreRepository extends JpaRepository<Score, UUID> {
    Optional<Score> findBySeasonIdAndUserId(UUID seasonId, UUID userId);
}
