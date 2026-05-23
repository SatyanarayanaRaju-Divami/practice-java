package com.example.practicejava.scoring.dto;

import com.example.practicejava.scoring.PredictionType;
import com.example.practicejava.scoring.ScoreBreakdown;

import java.util.UUID;

public record ScoreBreakdownResponse(
        UUID matchId,
        PredictionType predictionType,
        int pointsEarned
) {
    public static ScoreBreakdownResponse from(ScoreBreakdown sb) {
        return new ScoreBreakdownResponse(
                sb.getMatch() != null ? sb.getMatch().getId() : null,
                sb.getPredictionType(),
                sb.getPointsEarned()
        );
    }
}
