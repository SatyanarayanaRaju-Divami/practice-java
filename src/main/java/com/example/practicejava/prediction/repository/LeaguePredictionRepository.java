package com.example.practicejava.prediction.repository;

import com.example.practicejava.prediction.LeaguePrediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LeaguePredictionRepository extends JpaRepository<LeaguePrediction, UUID> {
    List<LeaguePrediction> findBySeasonIdAndUserId(UUID seasonId, UUID userId);
    List<LeaguePrediction> findBySeasonId(UUID seasonId);
    Page<LeaguePrediction> findBySeasonId(UUID seasonId, Pageable pageable);
}
