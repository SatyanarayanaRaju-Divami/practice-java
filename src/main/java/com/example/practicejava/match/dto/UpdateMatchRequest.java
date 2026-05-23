package com.example.practicejava.match.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record UpdateMatchRequest(@NotNull Instant scheduledAt) {
}
