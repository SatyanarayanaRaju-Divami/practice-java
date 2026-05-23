package com.example.practicejava.team.dto;

import com.example.practicejava.team.Team;

import java.time.Instant;
import java.util.UUID;

public record TeamResponse(UUID id, String name, String logoUrl, Instant createdAt) {
    public static TeamResponse from(Team team) {
        return new TeamResponse(team.getId(), team.getName(), team.getLogoUrl(), team.getCreatedAt());
    }
}
