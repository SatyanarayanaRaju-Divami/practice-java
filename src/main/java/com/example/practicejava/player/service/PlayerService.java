package com.example.practicejava.player.service;

import com.example.practicejava.player.Player;
import com.example.practicejava.player.PlayerNotFoundException;
import com.example.practicejava.player.dto.CreatePlayerRequest;
import com.example.practicejava.player.dto.UpdatePlayerRequest;
import com.example.practicejava.player.repository.PlayerRepository;
import com.example.practicejava.team.Team;
import com.example.practicejava.team.service.TeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamService teamService;

    public PlayerService(PlayerRepository playerRepository, TeamService teamService) {
        this.playerRepository = playerRepository;
        this.teamService = teamService;
    }

    @Transactional(readOnly = true)
    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Player findById(UUID id) {
        return playerRepository.findById(id).orElseThrow(() -> new PlayerNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Player> findByTeamId(UUID teamId) {
        return playerRepository.findByTeam_Id(teamId);
    }

    public Player create(CreatePlayerRequest request) {
        Team team = teamService.findById(request.teamId());
        Player player = new Player(team, request.name());
        player.setJerseyNumber(request.jerseyNumber());
        return playerRepository.save(player);
    }

    public Player update(UUID id, UpdatePlayerRequest request) {
        Player player = findById(id);
        player.setName(request.name());
        player.setJerseyNumber(request.jerseyNumber());
        if (request.teamId() != null) {
            Team team = teamService.findById(request.teamId());
            player.setTeam(team);
        }
        return player;
    }

    public void delete(UUID id) {
        Player player = findById(id);
        player.softDelete(null); // populated from AuditorAware once JWT is wired
    }
}
