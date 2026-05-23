package com.example.practicejava.team.service;

import com.example.practicejava.team.Team;
import com.example.practicejava.team.TeamNotFoundException;
import com.example.practicejava.team.dto.CreateTeamRequest;
import com.example.practicejava.team.dto.UpdateTeamRequest;
import com.example.practicejava.team.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Transactional(readOnly = true)
    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Team findById(UUID id) {
        return teamRepository.findById(id).orElseThrow(() -> new TeamNotFoundException(id));
    }

    public Team create(CreateTeamRequest request) {
        Team team = new Team(request.name());
        team.setLogoUrl(request.logoUrl());
        return teamRepository.save(team);
    }

    public Team update(UUID id, UpdateTeamRequest request) {
        Team team = findById(id);
        team.setName(request.name());
        team.setLogoUrl(request.logoUrl());
        return team;
    }

    public void delete(UUID id) {
        if (!teamRepository.existsById(id)) {
            throw new TeamNotFoundException(id);
        }
        teamRepository.deleteById(id);
    }
}
