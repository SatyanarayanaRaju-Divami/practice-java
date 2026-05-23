package com.example.practicejava.match;

import com.example.practicejava.league.Season;
import com.example.practicejava.league.SeasonNotFoundException;
import com.example.practicejava.league.SeasonRepository;
import com.example.practicejava.match.dto.CreateMatchRequest;
import com.example.practicejava.match.dto.UpdateMatchRequest;
import com.example.practicejava.team.Team;
import com.example.practicejava.team.TeamNotFoundException;
import com.example.practicejava.team.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MatchService {

    private final MatchRepository matchRepository;
    private final SeasonRepository seasonRepository;
    private final TeamRepository teamRepository;

    public MatchService(MatchRepository matchRepository,
                        SeasonRepository seasonRepository,
                        TeamRepository teamRepository) {
        this.matchRepository = matchRepository;
        this.seasonRepository = seasonRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional(readOnly = true)
    public List<Match> findAll() {
        return matchRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Match findById(UUID id) {
        return matchRepository.findById(id).orElseThrow(() -> new MatchNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Match> findBySeasonId(UUID seasonId) {
        return matchRepository.findBySeasonId(seasonId);
    }

    public Match create(UUID seasonId, CreateMatchRequest request) {
        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new SeasonNotFoundException(seasonId));
        Team homeTeam = teamRepository.findById(request.homeTeamId())
                .orElseThrow(() -> new TeamNotFoundException(request.homeTeamId()));
        Team awayTeam = teamRepository.findById(request.awayTeamId())
                .orElseThrow(() -> new TeamNotFoundException(request.awayTeamId()));
        Match match = new Match(season, homeTeam, awayTeam, request.scheduledAt(), request.scheduledAt());
        return matchRepository.save(match);
    }

    public Match update(UUID id, UpdateMatchRequest request) {
        Match match = findById(id);
        match.setScheduledAt(request.scheduledAt());
        match.setLockTime(request.scheduledAt());
        return match;
    }

    public void delete(UUID id) {
        if (!matchRepository.existsById(id)) {
            throw new MatchNotFoundException(id);
        }
        matchRepository.deleteById(id);
    }
}
