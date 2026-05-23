package com.example.practicejava.match.service;

import com.example.practicejava.appconfig.service.AppConfigService;
import com.example.practicejava.league.Season;
import com.example.practicejava.league.service.SeasonService;
import com.example.practicejava.match.Match;
import com.example.practicejava.match.MatchNotFoundException;
import com.example.practicejava.match.MatchNotModifiableException;
import com.example.practicejava.match.MatchStatus;
import com.example.practicejava.match.SameTeamMatchException;
import com.example.practicejava.match.TeamNotInSeasonException;
import com.example.practicejava.match.dto.CreateMatchRequest;
import com.example.practicejava.match.dto.UpdateMatchRequest;
import com.example.practicejava.match.repository.MatchRepository;
import com.example.practicejava.team.Team;
import com.example.practicejava.team.repository.SeasonTeamRepository;
import com.example.practicejava.team.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MatchService {

    private static final Logger log = LoggerFactory.getLogger(MatchService.class);

    private final MatchRepository matchRepository;
    private final SeasonService seasonService;
    private final TeamService teamService;
    private final SeasonTeamRepository seasonTeamRepository;
    private final AppConfigService appConfigService;

    public MatchService(MatchRepository matchRepository,
                        SeasonService seasonService,
                        TeamService teamService,
                        SeasonTeamRepository seasonTeamRepository,
                        AppConfigService appConfigService) {
        this.matchRepository = matchRepository;
        this.seasonService = seasonService;
        this.teamService = teamService;
        this.seasonTeamRepository = seasonTeamRepository;
        this.appConfigService = appConfigService;
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

    @Transactional(readOnly = true)
    public Page<Match> findBySeasonId(UUID seasonId, Pageable pageable) {
        seasonService.findById(seasonId); // validate exists
        return matchRepository.findBySeasonId(seasonId, pageable);
    }

    public Match create(UUID seasonId, CreateMatchRequest request) {
        if (request.homeTeamId().equals(request.awayTeamId())) {
            throw new SameTeamMatchException();
        }

        Season season = seasonService.findById(seasonId);
        Team homeTeam = teamService.findById(request.homeTeamId());
        Team awayTeam = teamService.findById(request.awayTeamId());

        if (!seasonTeamRepository.existsBySeasonIdAndTeamId(seasonId, request.homeTeamId())) {
            throw new TeamNotInSeasonException(request.homeTeamId(), seasonId);
        }
        if (!seasonTeamRepository.existsBySeasonIdAndTeamId(seasonId, request.awayTeamId())) {
            throw new TeamNotInSeasonException(request.awayTeamId(), seasonId);
        }

        Instant lockTime = calculateLockTime(request.scheduledAt());
        Match match = new Match(season, homeTeam, awayTeam, request.scheduledAt(), lockTime);
        match = matchRepository.save(match);

        updateFirstMatchStartTime(season, request.scheduledAt());

        log.info("Match created: id={} seasonId={} home={} away={}", match.getId(), seasonId,
                homeTeam.getName(), awayTeam.getName());
        return match;
    }

    public Match update(UUID id, UpdateMatchRequest request) {
        Match match = findById(id);
        if (match.getStatus() != MatchStatus.SCHEDULED) {
            throw new MatchNotModifiableException(id, match.getStatus());
        }
        match.setScheduledAt(request.scheduledAt());
        match.setLockTime(calculateLockTime(request.scheduledAt()));

        // Flush the new scheduledAt before querying for the season minimum
        matchRepository.flush();
        recalculateFirstMatchStartTime(match.getSeason());

        log.info("Match updated: id={}", id);
        return match;
    }

    public void delete(UUID id, UUID deletedBy) {
        Match match = findById(id);
        match.softDelete(deletedBy);
        log.info("Match deleted: id={}", id);
    }

    private Instant calculateLockTime(Instant scheduledAt) {
        long offsetHours = Long.parseLong(
                appConfigService.findByKey("match.lock.offset.hours").getValue());
        return scheduledAt.minus(offsetHours, ChronoUnit.HOURS);
    }

    private void updateFirstMatchStartTime(Season season, Instant scheduledAt) {
        if (season.getFirstMatchStartTime() == null
                || scheduledAt.isBefore(season.getFirstMatchStartTime())) {
            season.setFirstMatchStartTime(scheduledAt);
        }
    }

    private void recalculateFirstMatchStartTime(Season season) {
        matchRepository.findEarliestScheduledAtBySeasonId(season.getId())
                .ifPresent(season::setFirstMatchStartTime);
    }
}
