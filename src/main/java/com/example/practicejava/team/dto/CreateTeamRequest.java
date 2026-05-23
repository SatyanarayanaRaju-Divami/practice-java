package com.example.practicejava.team.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTeamRequest(@NotBlank String name, String logoUrl) {
}
