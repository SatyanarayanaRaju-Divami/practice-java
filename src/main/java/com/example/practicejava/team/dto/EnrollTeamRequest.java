package com.example.practicejava.team.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EnrollTeamRequest(
        @NotNull UUID teamId,
        Integer seedPosition
) {}
