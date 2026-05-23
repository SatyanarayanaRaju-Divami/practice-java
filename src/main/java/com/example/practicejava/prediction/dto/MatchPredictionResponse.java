package com.example.practicejava.prediction.dto;

import com.example.practicejava.match.MatchResult;
import com.example.practicejava.prediction.MatchPrediction;

import java.time.Instant;
import java.util.Optional;
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
        Instant submittedAt,
        // null = result not yet published; true/false = correct/incorrect
        Boolean winnerCorrect,
        Boolean tossCorrect,
        Boolean potmCorrect
) {
    public static MatchPredictionResponse from(MatchPrediction p) {
        return from(p, Optional.empty());
    }

    public static MatchPredictionResponse from(MatchPrediction p, Optional<MatchResult> result) {
        Boolean winnerCorrect = null;
        Boolean tossCorrect = null;
        Boolean potmCorrect = null;

        if (result.isPresent()) {
            MatchResult r = result.get();

            if (r.isDraw()) {
                winnerCorrect = p.getPredictedWinnerTeam() == null;
            } else if (r.getWinnerTeam() != null && p.getPredictedWinnerTeam() != null) {
                winnerCorrect = r.getWinnerTeam().getId().equals(p.getPredictedWinnerTeam().getId());
            } else {
                winnerCorrect = false;
            }

            if (r.getTossWinnerTeam() != null && p.getPredictedTossWinner() != null) {
                tossCorrect = r.getTossWinnerTeam().getId().equals(p.getPredictedTossWinner().getId());
            } else {
                tossCorrect = false;
            }

            if (r.getPlayerOfMatch() != null && p.getPredictedPotmPlayer() != null) {
                potmCorrect = r.getPlayerOfMatch().getId().equals(p.getPredictedPotmPlayer().getId());
            } else {
                potmCorrect = false;
            }
        }

        return new MatchPredictionResponse(
                p.getMatch().getId(),
                p.getUser().getId(),
                p.getUser().getDisplayName(),
                p.getPredictedWinnerTeam() != null ? p.getPredictedWinnerTeam().getId() : null,
                p.getPredictedWinnerTeam() != null ? p.getPredictedWinnerTeam().getName() : null,
                p.getPredictedTossWinner() != null ? p.getPredictedTossWinner().getId() : null,
                p.getPredictedTossWinner() != null ? p.getPredictedTossWinner().getName() : null,
                p.getPredictedPotmPlayer() != null ? p.getPredictedPotmPlayer().getId() : null,
                p.getPredictedPotmPlayer() != null ? p.getPredictedPotmPlayer().getName() : null,
                p.getSubmittedAt(),
                winnerCorrect,
                tossCorrect,
                potmCorrect
        );
    }
}
