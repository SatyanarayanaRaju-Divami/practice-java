package com.example.practicejava.team.service;

import com.example.practicejava.team.Team;
import com.example.practicejava.team.TeamNotFoundException;
import com.example.practicejava.team.dto.CreateTeamRequest;
import com.example.practicejava.team.dto.UpdateTeamRequest;
import com.example.practicejava.common.CacheConfig;
import com.example.practicejava.team.repository.TeamRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<Team> findAll(Pageable pageable) {
        return teamRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.CACHE_TEAMS, key = "#id")
    public Team findById(UUID id) {
        return teamRepository.findById(id).orElseThrow(() -> new TeamNotFoundException(id));
    }

    @CacheEvict(value = CacheConfig.CACHE_TEAMS, allEntries = true)
    public Team create(CreateTeamRequest request) {
        Team team = new Team(request.name());
        team.setLogoUrl(request.logoUrl());
        return teamRepository.save(team);
    }

    @CacheEvict(value = CacheConfig.CACHE_TEAMS, allEntries = true)
    public Team update(UUID id, UpdateTeamRequest request) {
        Team team = findById(id);
        team.setName(request.name());
        team.setLogoUrl(request.logoUrl());
        return team;
    }

    @CacheEvict(value = CacheConfig.CACHE_TEAMS, allEntries = true)
    public void delete(UUID id, UUID deletedBy) {
        Team team = findById(id);
        team.softDelete(deletedBy);
    }
}
