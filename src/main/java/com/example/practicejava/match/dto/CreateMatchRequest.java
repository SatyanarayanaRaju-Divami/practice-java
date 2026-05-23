package com.example.practicejava.match.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record CreateMatchRequest(@NotNull UUID homeTeamId, @NotNull UUID awayTeamId, @NotNull Instant scheduledAt) {
}
