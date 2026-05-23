package com.example.practicejava.standing.dto;

import com.example.practicejava.standing.LeagueStanding;

import java.util.UUID;

public record LeagueStandingResponse(
        UUID id,
        UUID seasonId,
        UUID teamId,
        String teamName,
        Integer currentPosition,
        int matchesPlayed,
        int wins,
        int draws,
        int losses,
        int pointsInLeague
) {
    public static LeagueStandingResponse from(LeagueStanding standing) {
        return new LeagueStandingResponse(
                standing.getId(),
                standing.getSeason().getId(),
                standing.getTeam().getId(),
                standing.getTeam().getName(),
                standing.getCurrentPosition(),
                standing.getMatchesPlayed(),
                standing.getWins(),
                standing.getDraws(),
                standing.getLosses(),
                standing.getPointsInLeague()
        );
    }
}
