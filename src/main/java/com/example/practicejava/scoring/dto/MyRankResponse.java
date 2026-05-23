package com.example.practicejava.scoring.dto;

import com.example.practicejava.scoring.Leaderboard;
import com.example.practicejava.scoring.ScoreBreakdown;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MyRankResponse(
        UUID userId,
        String displayName,
        int rank,
        int totalPoints,
        Instant lastCalculatedAt,
        List<ScoreBreakdownResponse> breakdown
) {
    public static MyRankResponse from(Leaderboard entry, List<ScoreBreakdown> breakdowns) {
        return new MyRankResponse(
                entry.getUser().getId(),
                entry.getUser().getDisplayName(),
                entry.getRank(),
                entry.getTotalPoints(),
                entry.getLastCalculatedAt(),
                breakdowns.stream().map(ScoreBreakdownResponse::from).toList()
        );
    }
}
