package com.example.practicejava.standing;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class LeagueStandingService {

    private final LeagueStandingRepository leagueStandingRepository;

    public LeagueStandingService(LeagueStandingRepository leagueStandingRepository) {
        this.leagueStandingRepository = leagueStandingRepository;
    }

    @Transactional(readOnly = true)
    public List<LeagueStanding> findBySeasonId(UUID seasonId) {
        return leagueStandingRepository.findBySeasonIdOrderByCurrentPositionAsc(seasonId);
    }
}
