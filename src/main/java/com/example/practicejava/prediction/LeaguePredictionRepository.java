package com.example.practicejava.prediction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LeaguePredictionRepository extends JpaRepository<LeaguePrediction, UUID> {
    List<LeaguePrediction> findBySeasonIdAndUserId(UUID seasonId, UUID userId);
    List<LeaguePrediction> findBySeasonId(UUID seasonId);
}
