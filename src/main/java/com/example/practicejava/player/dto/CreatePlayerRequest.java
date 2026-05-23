package com.example.practicejava.player.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreatePlayerRequest(
        @NotNull UUID teamId,
        @NotBlank String name,
        Integer jerseyNumber
) {}
