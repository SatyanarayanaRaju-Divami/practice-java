package com.example.practicejava.prediction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchPredictionRepository extends JpaRepository<MatchPrediction, UUID> {
    Optional<MatchPrediction> findByMatchIdAndUserId(UUID matchId, UUID userId);
    List<MatchPrediction> findByMatchId(UUID matchId);
}
