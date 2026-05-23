package com.example.practicejava.league.dto;

import com.example.practicejava.league.League;

import java.time.Instant;
import java.util.UUID;

public record LeagueResponse(UUID id, String name, String description, Instant createdAt) {
    public static LeagueResponse from(League league) {
        return new LeagueResponse(league.getId(), league.getName(), league.getDescription(), league.getCreatedAt());
    }
}
