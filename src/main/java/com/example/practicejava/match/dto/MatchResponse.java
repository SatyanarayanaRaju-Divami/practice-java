package com.example.practicejava.match.dto;

import com.example.practicejava.match.Match;
import com.example.practicejava.match.MatchStatus;

import java.time.Instant;
import java.util.UUID;

public record MatchResponse(
        UUID id,
        UUID seasonId,
        UUID homeTeamId,
        String homeTeamName,
        UUID awayTeamId,
        String awayTeamName,
        Instant scheduledAt,
        Instant lockTime,
        MatchStatus status
) {
    public static MatchResponse from(Match match) {
        return new MatchResponse(
                match.getId(),
                match.getSeason().getId(),
                match.getHomeTeam().getId(),
                match.getHomeTeam().getName(),
                match.getAwayTeam().getId(),
                match.getAwayTeam().getName(),
                match.getScheduledAt(),
                match.getLockTime(),
                match.getStatus()
        );
    }
}
