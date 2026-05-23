package com.example.practicejava.league.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateLeagueRequest(@NotBlank String name, String description) {
}
