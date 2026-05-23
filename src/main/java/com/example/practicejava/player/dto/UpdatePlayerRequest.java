package com.example.practicejava.player.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdatePlayerRequest(
        @NotBlank String name,
        Integer jerseyNumber,
        UUID teamId   // optional: supply to transfer player to a different team
) {}
