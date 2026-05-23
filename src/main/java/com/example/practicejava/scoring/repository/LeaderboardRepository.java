package com.example.practicejava.scoring.repository;

import com.example.practicejava.scoring.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaderboardRepository extends JpaRepository<Leaderboard, UUID> {
    List<Leaderboard> findBySeasonIdOrderByRankAsc(UUID seasonId);
    Optional<Leaderboard> findBySeasonIdAndUserId(UUID seasonId, UUID userId);
}
