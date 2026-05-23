package com.example.practicejava.scoring.dto;

import com.example.practicejava.scoring.Leaderboard;

import java.time.Instant;
import java.util.UUID;

public record LeaderboardEntryResponse(int rank, UUID userId, String displayName, int totalPoints,
                                        Instant lastCalculatedAt) {
    public static LeaderboardEntryResponse from(Leaderboard leaderboard) {
        return new LeaderboardEntryResponse(
                leaderboard.getRank(),
                leaderboard.getUser().getId(),
                leaderboard.getUser().getName(),
                leaderboard.getTotalPoints(),
                leaderboard.getLastCalculatedAt()
        );
    }
}
