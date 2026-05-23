package com.example.practicejava.league;

import com.example.practicejava.league.dto.CreateLeagueRequest;
import com.example.practicejava.league.dto.UpdateLeagueRequest;
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
    public League findById(UUID id) {
        return leagueRepository.findById(id).orElseThrow(() -> new LeagueNotFoundException(id));
    }

    public League create(CreateLeagueRequest request) {
        return leagueRepository.save(new League(request.name(), request.description()));
    }

    public League update(UUID id, UpdateLeagueRequest request) {
        League league = findById(id);
        league.setName(request.name());
        league.setDescription(request.description());
        return league;
    }

    public void delete(UUID id) {
        if (!leagueRepository.existsById(id)) {
            throw new LeagueNotFoundException(id);
        }
        leagueRepository.deleteById(id);
    }
}
