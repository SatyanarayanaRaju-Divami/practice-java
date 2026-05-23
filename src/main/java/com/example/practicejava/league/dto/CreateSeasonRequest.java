package com.example.practicejava.league.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSeasonRequest(@NotBlank String name) {
}
