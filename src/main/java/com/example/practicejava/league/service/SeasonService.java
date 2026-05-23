package com.example.practicejava.league.service;

import com.example.practicejava.appconfig.service.AppConfigService;
import com.example.practicejava.league.League;
import com.example.practicejava.league.Season;
import com.example.practicejava.league.SeasonClosedException;
import com.example.practicejava.league.SeasonNotActivatableException;
import com.example.practicejava.league.SeasonNotClosableException;
import com.example.practicejava.league.SeasonNotFoundException;
import com.example.practicejava.league.SeasonStatus;
import com.example.practicejava.league.dto.CreateSeasonRequest;
import com.example.practicejava.league.repository.SeasonRepository;
import com.example.practicejava.match.repository.MatchRepository;
import com.example.practicejava.team.repository.SeasonTeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SeasonService {

    private static final Logger log = LoggerFactory.getLogger(SeasonService.class);

    private final SeasonRepository seasonRepository;
    private final LeagueService leagueService;
    private final SeasonTeamRepository seasonTeamRepository;
    private final MatchRepository matchRepository;
    private final AppConfigService appConfigService;

    public SeasonService(SeasonRepository seasonRepository,
                         LeagueService leagueService,
                         SeasonTeamRepository seasonTeamRepository,
                         MatchRepository matchRepository,
                         AppConfigService appConfigService) {
        this.seasonRepository = seasonRepository;
        this.leagueService = leagueService;
        this.seasonTeamRepository = seasonTeamRepository;
        this.matchRepository = matchRepository;
        this.appConfigService = appConfigService;
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

    @Transactional(readOnly = true)
    public Page<Season> findByLeagueId(UUID leagueId, Pageable pageable) {
        leagueService.findById(leagueId); // validate exists
        return seasonRepository.findByLeagueId(leagueId, pageable);
    }

    public Season create(UUID leagueId, CreateSeasonRequest request) {
        League league = leagueService.findById(leagueId);
        return seasonRepository.save(new Season(league, request.name()));
    }

    // ─── US-009: Activate season ──────────────────────────────────────────────

    public Season activate(UUID id) {
        Season season = findById(id);

        if (season.getStatus() != SeasonStatus.DRAFT) {
            throw new SeasonNotActivatableException("season must be in DRAFT status");
        }
        if (seasonTeamRepository.countBySeasonId(id) == 0) {
            throw new SeasonNotActivatableException("season has no enrolled teams");
        }
        if (matchRepository.countBySeasonId(id) == 0) {
            throw new SeasonNotActivatableException("season has no scheduled matches");
        }
        if (season.getFirstMatchStartTime() == null) {
            throw new SeasonNotActivatableException("first match start time is not set");
        }

        long lockOffsetHours = Long.parseLong(
                appConfigService.findByKey("league.lock.offset.hours").getValue());
        season.setLeagueLockTime(
                season.getFirstMatchStartTime().minus(lockOffsetHours, ChronoUnit.HOURS));

        season.setStatus(SeasonStatus.OPEN);
        log.info("Season activated: id={} leagueLockTime={}", id, season.getLeagueLockTime());
        return season;
    }

    // ─── US-012: Close season ─────────────────────────────────────────────────

    public Season close(UUID id) {
        Season season = findById(id);
        if (season.getStatus() != SeasonStatus.COMPLETED) {
            throw new SeasonNotClosableException(id);
        }
        season.setStatus(SeasonStatus.CLOSED);
        log.info("Season closed: id={}", id);
        return season;
    }

    public Season complete(UUID id) {
        Season season = findById(id);
        season.setStatus(SeasonStatus.COMPLETED);
        return season;
    }

    public void delete(UUID id, UUID deletedBy) {
        Season season = findById(id);
        season.softDelete(deletedBy);
    }

    /** Guard used by result publishing and league prediction submission. */
    public void assertNotClosed(Season season) {
        if (season.getStatus() == SeasonStatus.CLOSED) {
            throw new SeasonClosedException(season.getId());
        }
    }
}
