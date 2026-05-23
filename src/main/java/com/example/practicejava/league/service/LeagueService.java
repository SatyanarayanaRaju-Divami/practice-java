package com.example.practicejava.league.service;

import com.example.practicejava.league.League;
import com.example.practicejava.league.LeagueNotFoundException;
import com.example.practicejava.common.CacheConfig;
import com.example.practicejava.league.dto.CreateLeagueRequest;
import com.example.practicejava.league.dto.UpdateLeagueRequest;
import com.example.practicejava.league.repository.LeagueRepository;
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
public class LeagueService {

    private final LeagueRepository leagueRepository;

    public LeagueService(LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
    }

    @Transactional(readOnly = true)
    public List<League> findAll() {
        return leagueRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<League> findAll(Pageable pageable) {
        return leagueRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.CACHE_LEAGUES, key = "#id")
    public League findById(UUID id) {
        return leagueRepository.findById(id).orElseThrow(() -> new LeagueNotFoundException(id));
    }

    @CacheEvict(value = CacheConfig.CACHE_LEAGUES, allEntries = true)
    public League create(CreateLeagueRequest request) {
        return leagueRepository.save(new League(request.name(), request.description()));
    }

    @CacheEvict(value = CacheConfig.CACHE_LEAGUES, allEntries = true)
    public League update(UUID id, UpdateLeagueRequest request) {
        League league = findById(id);
        league.setName(request.name());
        league.setDescription(request.description());
        return league;
    }

    @CacheEvict(value = CacheConfig.CACHE_LEAGUES, allEntries = true)
    public void delete(UUID id, UUID deletedBy) {
        League league = findById(id);
        league.softDelete(deletedBy);
    }
}
