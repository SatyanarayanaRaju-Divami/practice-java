package com.example.practicejava.team.dto;

import com.example.practicejava.team.SeasonTeam;

import java.util.UUID;

public record SeasonTeamResponse(UUID id, UUID seasonId, UUID teamId, String teamName, Integer seedPosition) {
    public static SeasonTeamResponse from(SeasonTeam st) {
        return new SeasonTeamResponse(
                st.getId(),
                st.getSeason().getId(),
                st.getTeam().getId(),
                st.getTeam().getName(),
                st.getSeedPosition()
        );
    }
}
