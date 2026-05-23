package com.example.practicejava.prediction.dto;

import java.util.UUID;

public record SubmitMatchPredictionRequest(UUID predictedWinnerTeamId, UUID predictedTossWinnerId,
                                           UUID predictedPotmPlayerId) {
}
