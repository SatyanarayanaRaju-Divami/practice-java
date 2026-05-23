package com.example.practicejava.league.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.UUID;

public record PublishFinalStandingsRequest(@NotEmpty List<Entry> standings) {

    public record Entry(@NotNull UUID teamId, @NotNull @Positive Integer finalPosition) {}
}
