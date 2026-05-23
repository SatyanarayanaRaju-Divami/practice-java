package com.example.practicejava.league.service;

import com.example.practicejava.league.League;
import com.example.practicejava.league.Season;
import com.example.practicejava.league.SeasonNotFoundException;
import com.example.practicejava.league.SeasonStatus;
import com.example.practicejava.league.dto.CreateSeasonRequest;
import com.example.practicejava.league.repository.SeasonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SeasonService {

    private final SeasonRepository seasonRepository;
    private final LeagueService leagueService;

    public SeasonService(SeasonRepository seasonRepository, LeagueService leagueService) {
        this.seasonRepository = seasonRepository;
        this.leagueService = leagueService;
    }

    @Transactional(readOnly = true)
    public List<Season> findAll() {
        return seasonRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Season findById(UUID id) {
        return seasonRepository.findById(id).orElseThrow(() -> new SeasonNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Season> findByLeagueId(UUID leagueId) {
        return seasonRepository.findByLeagueId(leagueId);
    }

    public Season create(UUID leagueId, CreateSeasonRequest request) {
        League league = leagueService.findById(leagueId);
        return seasonRepository.save(new Season(league, request.name()));
    }

    public Season activate(UUID id) {
        Season season = findById(id);
        season.setStatus(SeasonStatus.OPEN);
        return season;
    }

    public Season close(UUID id) {
        Season season = findById(id);
        season.setStatus(SeasonStatus.CLOSED);
        return season;
    }

    public void delete(UUID id) {
        if (!seasonRepository.existsById(id)) {
            throw new SeasonNotFoundException(id);
        }
        seasonRepository.deleteById(id);
    }
}
