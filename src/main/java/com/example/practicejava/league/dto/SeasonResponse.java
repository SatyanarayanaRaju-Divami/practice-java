package com.example.practicejava.league.dto;

import com.example.practicejava.league.Season;
import com.example.practicejava.league.SeasonStatus;

import java.time.Instant;
import java.util.UUID;

public record SeasonResponse(
        UUID id,
        UUID leagueId,
        String leagueName,
        String name,
        SeasonStatus status,
        Instant firstMatchStartTime,
        Instant leagueLockTime,
        Instant createdAt
) {
    public static SeasonResponse from(Season season) {
        return new SeasonResponse(
                season.getId(),
                season.getLeague().getId(),
                season.getLeague().getName(),
                season.getName(),
                season.getStatus(),
                season.getFirstMatchStartTime(),
                season.getLeagueLockTime(),
                season.getCreatedAt()
        );
    }
}
