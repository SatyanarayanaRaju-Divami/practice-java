package com.example.practicejava.scoring.repository;

import com.example.practicejava.scoring.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaderboardRepository extends JpaRepository<Leaderboard, UUID> {
    List<Leaderboard> findBySeasonIdOrderByRankAsc(UUID seasonId);
    Page<Leaderboard> findBySeasonId(UUID seasonId, Pageable pageable);
    Optional<Leaderboard> findBySeasonIdAndUserId(UUID seasonId, UUID userId);
}
