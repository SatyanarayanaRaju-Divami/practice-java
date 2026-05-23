package com.example.practicejava.match.service;

import com.example.practicejava.league.Season;
import com.example.practicejava.league.dto.PublishFinalStandingsRequest;
import com.example.practicejava.league.service.SeasonService;
import com.example.practicejava.match.Match;
import com.example.practicejava.match.MatchAlreadyCompletedException;
import com.example.practicejava.match.MatchResult;
import com.example.practicejava.match.MatchStatus;
import com.example.practicejava.match.ResultNotFoundException;
import com.example.practicejava.match.dto.PublishResultRequest;
import com.example.practicejava.match.repository.MatchResultRepository;
import com.example.practicejava.player.Player;
import com.example.practicejava.player.PlayerNotFoundException;
import com.example.practicejava.player.repository.PlayerRepository;
import com.example.practicejava.prediction.InvalidPredictionPlayerException;
import com.example.practicejava.prediction.InvalidPredictionTeamException;
import com.example.practicejava.scoring.service.ScoreCalculationService;
import com.example.practicejava.standing.LeagueStanding;
import com.example.practicejava.standing.repository.LeagueStandingRepository;
import com.example.practicejava.team.Team;
import com.example.practicejava.team.TeamNotFoundException;
import com.example.practicejava.team.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResultService {

    private static final Logger log = LoggerFactory.getLogger(ResultService.class);

    private final MatchService matchService;
    private final SeasonService seasonService;
    private final MatchResultRepository matchResultRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final LeagueStandingRepository leagueStandingRepository;
    private final ScoreCalculationService scoreCalculationService;

    public ResultService(MatchService matchService,
                         SeasonService seasonService,
                         MatchResultRepository matchResultRepository,
                         TeamRepository teamRepository,
                         PlayerRepository playerRepository,
                         LeagueStandingRepository leagueStandingRepository,
                         ScoreCalculationService scoreCalculationService) {
        this.matchService = matchService;
        this.seasonService = seasonService;
        this.matchResultRepository = matchResultRepository;
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.leagueStandingRepository = leagueStandingRepository;
        this.scoreCalculationService = scoreCalculationService;
    }

    // ─── US-023: Publish match result ────────────────────────────────────────

    public MatchResult publishMatchResult(UUID matchId, UUID publishedBy, PublishResultRequest request) {
        Match match = matchService.findById(matchId);
        seasonService.assertNotClosed(match.getSeason());

        if (match.getStatus() == MatchStatus.COMPLETED) {
            throw new MatchAlreadyCompletedException(matchId);
        }

        Set<UUID> matchTeamIds = Set.of(match.getHomeTeam().getId(), match.getAwayTeam().getId());

        if (!request.isDraw() && request.winnerTeamId() != null
                && !matchTeamIds.contains(request.winnerTeamId())) {
            throw new InvalidPredictionTeamException(request.winnerTeamId(), matchId);
        }
        if (!matchTeamIds.contains(request.tossWinnerTeamId())) {
            throw new InvalidPredictionTeamException(request.tossWinnerTeamId(), matchId);
        }

        Player potm = playerRepository.findById(request.playerOfMatchId())
                .orElseThrow(() -> new PlayerNotFoundException(request.playerOfMatchId()));
        if (!matchTeamIds.contains(potm.getTeam().getId())) {
            throw new InvalidPredictionPlayerException(request.playerOfMatchId(), matchId);
        }

        MatchResult result = new MatchResult(match);
        result.setDraw(request.isDraw());

        if (!request.isDraw() && request.winnerTeamId() != null) {
            Team winner = teamRepository.findById(request.winnerTeamId())
                    .orElseThrow(() -> new TeamNotFoundException(request.winnerTeamId()));
            result.setWinnerTeam(winner);
        }

        Team tossWinner = teamRepository.findById(request.tossWinnerTeamId())
                .orElseThrow(() -> new TeamNotFoundException(request.tossWinnerTeamId()));
        result.setTossWinnerTeam(tossWinner);
        result.setPlayerOfMatch(potm);
        result.setPublishedAt(Instant.now());
        result.setPublishedBy(publishedBy);

        match.setStatus(MatchStatus.COMPLETED);

        updateLeagueStandings(match, result);

        MatchResult saved = matchResultRepository.save(result);

        log.info("Match result published: matchId={}", matchId);
        scoreCalculationService.calculateMatchScores(saved);

        return saved;
    }

    // ─── US-024: View match result ────────────────────────────────────────────

    @Transactional(readOnly = true)
    public MatchResult getMatchResult(UUID matchId) {
        matchService.findById(matchId);
        return matchResultRepository.findByMatchId(matchId)
                .orElseThrow(() -> new ResultNotFoundException(matchId));
    }

    // ─── US-025: Publish final league standings ───────────────────────────────

    public void publishFinalStandings(UUID seasonId, UUID publishedBy,
                                      PublishFinalStandingsRequest request) {
        Season season = seasonService.findById(seasonId);
        seasonService.assertNotClosed(season);

        Map<UUID, Integer> finalPositions = request.standings().stream()
                .collect(Collectors.toMap(
                        PublishFinalStandingsRequest.Entry::teamId,
                        PublishFinalStandingsRequest.Entry::finalPosition));

        for (PublishFinalStandingsRequest.Entry entry : request.standings()) {
            LeagueStanding standing = leagueStandingRepository
                    .findBySeasonIdAndTeamId(seasonId, entry.teamId())
                    .orElseGet(() -> {
                        Team team = teamRepository.findById(entry.teamId())
                                .orElseThrow(() -> new TeamNotFoundException(entry.teamId()));
                        return new LeagueStanding(season, team);
                    });
            standing.setCurrentPosition(entry.finalPosition());
            leagueStandingRepository.save(standing);
        }

        seasonService.complete(seasonId);

        log.info("Final standings published: seasonId={}", seasonId);
        scoreCalculationService.calculateLeagueScores(seasonId, finalPositions);
    }

    // ─── Internal: update league standings after a match ─────────────────────

    private void updateLeagueStandings(Match match, MatchResult result) {
        Season season = match.getSeason();

        LeagueStanding homeStanding = getOrCreateStanding(season, match.getHomeTeam());
        LeagueStanding awayStanding = getOrCreateStanding(season, match.getAwayTeam());

        homeStanding.setMatchesPlayed(homeStanding.getMatchesPlayed() + 1);
        awayStanding.setMatchesPlayed(awayStanding.getMatchesPlayed() + 1);

        if (result.isDraw()) {
            homeStanding.setDraws(homeStanding.getDraws() + 1);
            homeStanding.setPointsInLeague(homeStanding.getPointsInLeague() + 1);
            awayStanding.setDraws(awayStanding.getDraws() + 1);
            awayStanding.setPointsInLeague(awayStanding.getPointsInLeague() + 1);
        } else if (result.getWinnerTeam() != null) {
            if (result.getWinnerTeam().getId().equals(match.getHomeTeam().getId())) {
                homeStanding.setWins(homeStanding.getWins() + 1);
                homeStanding.setPointsInLeague(homeStanding.getPointsInLeague() + 2);
                awayStanding.setLosses(awayStanding.getLosses() + 1);
            } else {
                awayStanding.setWins(awayStanding.getWins() + 1);
                awayStanding.setPointsInLeague(awayStanding.getPointsInLeague() + 2);
                homeStanding.setLosses(homeStanding.getLosses() + 1);
            }
        }

        recalculatePositions(season.getId());
    }

    private LeagueStanding getOrCreateStanding(Season season, Team team) {
        return leagueStandingRepository
                .findBySeasonIdAndTeamId(season.getId(), team.getId())
                .orElseGet(() -> leagueStandingRepository.save(new LeagueStanding(season, team)));
    }

    private void recalculatePositions(UUID seasonId) {
        List<LeagueStanding> standings = leagueStandingRepository.findBySeasonIdOrderByCurrentPositionAsc(seasonId);

        // sort by points DESC, then wins DESC
        List<LeagueStanding> sorted = standings.stream()
                .sorted(Comparator.comparingInt(LeagueStanding::getPointsInLeague).reversed()
                        .thenComparingInt(LeagueStanding::getWins).reversed())
                .toList();

        for (int i = 0; i < sorted.size(); i++) {
            sorted.get(i).setCurrentPosition(i + 1);
        }
    }
}
