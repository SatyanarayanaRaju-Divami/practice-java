package com.example.practicejava.match.dto;

import com.example.practicejava.match.MatchResult;

import java.time.Instant;
import java.util.UUID;

public record MatchResultResponse(
        UUID id,
        UUID matchId,
        UUID winnerTeamId,
        String winnerTeamName,
        UUID tossWinnerTeamId,
        String tossWinnerTeamName,
        UUID playerOfMatchId,
        String playerOfMatchName,
        boolean isDraw,
        Instant publishedAt
) {
    public static MatchResultResponse from(MatchResult result) {
        return new MatchResultResponse(
                result.getId(),
                result.getMatch().getId(),
                result.getWinnerTeam() != null ? result.getWinnerTeam().getId() : null,
                result.getWinnerTeam() != null ? result.getWinnerTeam().getName() : null,
                result.getTossWinnerTeam() != null ? result.getTossWinnerTeam().getId() : null,
                result.getTossWinnerTeam() != null ? result.getTossWinnerTeam().getName() : null,
                result.getPlayerOfMatch() != null ? result.getPlayerOfMatch().getId() : null,
                result.getPlayerOfMatch() != null ? result.getPlayerOfMatch().getName() : null,
                result.isDraw(),
                result.getPublishedAt()
        );
    }
}
