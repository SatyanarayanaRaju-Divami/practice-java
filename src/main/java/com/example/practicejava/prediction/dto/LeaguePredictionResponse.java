package com.example.practicejava.prediction.dto;

import com.example.practicejava.prediction.LeaguePrediction;

import java.time.Instant;
import java.util.UUID;

public record LeaguePredictionResponse(
        UUID seasonId,
        UUID userId,
        String userDisplayName,
        UUID teamId,
        String teamName,
        int predictedPosition,
        Instant submittedAt
) {
    public static LeaguePredictionResponse from(LeaguePrediction prediction) {
        return new LeaguePredictionResponse(
                prediction.getSeason().getId(),
                prediction.getUser().getId(),
                prediction.getUser().getName(),
                prediction.getTeam().getId(),
                prediction.getTeam().getName(),
                prediction.getPredictedPosition(),
                prediction.getSubmittedAt()
        );
    }
}
