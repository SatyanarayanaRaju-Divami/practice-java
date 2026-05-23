package com.example.practicejava.player.dto;

import com.example.practicejava.player.Player;

import java.util.UUID;

public record PlayerResponse(UUID id, UUID teamId, String teamName, String name, Integer jerseyNumber) {
    public static PlayerResponse from(Player player) {
        return new PlayerResponse(
                player.getId(),
                player.getTeam().getId(),
                player.getTeam().getName(),
                player.getName(),
                player.getJerseyNumber()
        );
    }
}
