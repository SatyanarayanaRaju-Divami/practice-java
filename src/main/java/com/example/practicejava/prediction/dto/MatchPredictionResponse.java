package com.example.practicejava.prediction.dto;

import com.example.practicejava.prediction.MatchPrediction;

import java.time.Instant;
import java.util.UUID;

public record MatchPredictionResponse(
        UUID matchId,
        UUID userId,
        String userDisplayName,
        UUID predictedWinnerTeamId,
        String predictedWinnerTeamName,
        UUID predictedTossWinnerId,
        String predictedTossWinnerName,
        UUID predictedPotmPlayerId,
        String predictedPotmPlayerName,
        Instant submittedAt
) {
    public static MatchPredictionResponse from(MatchPrediction prediction) {
        return new MatchPredictionResponse(
                prediction.getMatch().getId(),
                prediction.getUser().getId(),
                prediction.getUser().getName(),
                prediction.getPredictedWinnerTeam() != null ? prediction.getPredictedWinnerTeam().getId() : null,
                prediction.getPredictedWinnerTeam() != null ? prediction.getPredictedWinnerTeam().getName() : null,
                prediction.getPredictedTossWinner() != null ? prediction.getPredictedTossWinner().getId() : null,
                prediction.getPredictedTossWinner() != null ? prediction.getPredictedTossWinner().getName() : null,
                prediction.getPredictedPotmPlayer() != null ? prediction.getPredictedPotmPlayer().getId() : null,
                prediction.getPredictedPotmPlayer() != null ? prediction.getPredictedPotmPlayer().getName() : null,
                prediction.getSubmittedAt()
        );
    }
}
