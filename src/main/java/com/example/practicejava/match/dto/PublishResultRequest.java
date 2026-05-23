package com.example.practicejava.match.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PublishResultRequest(UUID winnerTeamId, @NotNull UUID tossWinnerTeamId,
                                   @NotNull UUID playerOfMatchId, boolean isDraw) {
}
