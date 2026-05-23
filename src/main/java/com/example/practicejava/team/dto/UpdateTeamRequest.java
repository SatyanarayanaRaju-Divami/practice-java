package com.example.practicejava.team.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateTeamRequest(@NotBlank String name, String logoUrl) {
}
