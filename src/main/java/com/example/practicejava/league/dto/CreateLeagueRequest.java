package com.example.practicejava.league.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateLeagueRequest(@NotBlank String name, String description) {
}
