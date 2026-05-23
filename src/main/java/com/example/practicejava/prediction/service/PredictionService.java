package com.example.practicejava.prediction.service;

import com.example.practicejava.league.Season;
import com.example.practicejava.league.service.SeasonService;
import com.example.practicejava.match.Match;
import com.example.practicejava.match.MatchResult;
import com.example.practicejava.match.MatchStatus;
import com.example.practicejava.match.repository.MatchResultRepository;
import com.example.practicejava.match.service.MatchService;
import com.example.practicejava.player.Player;
import com.example.practicejava.player.PlayerNotFoundException;
import com.example.practicejava.player.repository.PlayerRepository;
import com.example.practicejava.prediction.InvalidLeaguePredictionException;
import com.example.practicejava.prediction.InvalidPredictionPlayerException;
import com.example.practicejava.prediction.InvalidPredictionTeamException;
import com.example.practicejava.prediction.LeaguePrediction;
import com.example.practicejava.prediction.MatchPrediction;
import com.example.practicejava.prediction.PredictionLockedException;
import com.example.practicejava.prediction.PredictionWindowOpenException;
import com.example.practicejava.prediction.dto.SubmitLeaguePredictionRequest;
import com.example.practicejava.prediction.dto.SubmitMatchPredictionRequest;
import com.example.practicejava.prediction.repository.LeaguePredictionRepository;
import com.example.practicejava.prediction.repository.MatchPredictionRepository;
import com.example.practicejava.team.Team;
import com.example.practicejava.team.TeamNotFoundException;
import com.example.practicejava.team.repository.TeamRepository;
import com.example.practicejava.team.repository.SeasonTeamRepository;
import com.example.practicejava.user.User;
import com.example.practicejava.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PredictionService {

    private static final Logger log = LoggerFactory.getLogger(PredictionService.class);

    private final LeaguePredictionRepository leaguePredictionRepository;
    private final MatchPredictionRepository matchPredictionRepository;
    private final SeasonService seasonService;
    private final MatchService matchService;
    private final MatchResultRepository matchResultRepository;
    private final UserService userService;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final SeasonTeamRepository seasonTeamRepository;

    public PredictionService(LeaguePredictionRepository leaguePredictionRepository,
                             MatchPredictionRepository matchPredictionRepository,
                             SeasonService seasonService,
                             MatchService matchService,
                             MatchResultRepository matchResultRepository,
                             UserService userService,
                             TeamRepository teamRepository,
                             PlayerRepository playerRepository,
                             SeasonTeamRepository seasonTeamRepository) {
        this.leaguePredictionRepository = leaguePredictionRepository;
        this.matchPredictionRepository = matchPredictionRepository;
        this.seasonService = seasonService;
        this.matchService = matchService;
        this.matchResultRepository = matchResultRepository;
        this.userService = userService;
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.seasonTeamRepository = seasonTeamRepository;
    }

    // ─── US-017: Submit / update match prediction ────────────────────────────

    public MatchPrediction submitMatchPrediction(UUID matchId, UUID userId,
                                                  SubmitMatchPredictionRequest request) {
        Match match = matchService.findById(matchId);
        User user = userService.findById(userId);

        // Reject if prediction window is closed
        if (match.getStatus() != MatchStatus.SCHEDULED || Instant.now().isAfter(match.getLockTime())) {
            throw new PredictionLockedException(matchId);
        }

        Set<UUID> matchTeamIds = Set.of(match.getHomeTeam().getId(), match.getAwayTeam().getId());

        if (request.predictedWinnerTeamId() != null
                && !matchTeamIds.contains(request.predictedWinnerTeamId())) {
            throw new InvalidPredictionTeamException(request.predictedWinnerTeamId(), matchId);
        }
        if (request.predictedTossWinnerId() != null
                && !matchTeamIds.contains(request.predictedTossWinnerId())) {
            throw new InvalidPredictionTeamException(request.predictedTossWinnerId(), matchId);
        }
        if (request.predictedPotmPlayerId() != null) {
            Player potm = playerRepository.findById(request.predictedPotmPlayerId())
                    .orElseThrow(() -> new PlayerNotFoundException(request.predictedPotmPlayerId()));
            if (!matchTeamIds.contains(potm.getTeam().getId())) {
                throw new InvalidPredictionPlayerException(request.predictedPotmPlayerId(), matchId);
            }
        }

        MatchPrediction prediction = matchPredictionRepository
                .findByMatchIdAndUserId(matchId, userId)
                .orElse(new MatchPrediction(match, user));

        if (request.predictedWinnerTeamId() != null) {
            Team winner = teamRepository.findById(request.predictedWinnerTeamId())
                    .orElseThrow(() -> new TeamNotFoundException(request.predictedWinnerTeamId()));
            prediction.setPredictedWinnerTeam(winner);
        }
        if (request.predictedTossWinnerId() != null) {
            Team tossWinner = teamRepository.findById(request.predictedTossWinnerId())
                    .orElseThrow(() -> new TeamNotFoundException(request.predictedTossWinnerId()));
            prediction.setPredictedTossWinner(tossWinner);
        }
        if (request.predictedPotmPlayerId() != null) {
            Player potm = playerRepository.findById(request.predictedPotmPlayerId())
                    .orElseThrow(() -> new PlayerNotFoundException(request.predictedPotmPlayerId()));
            prediction.setPredictedPotmPlayer(potm);
        }

        prediction.setSubmittedAt(Instant.now());
        log.info("Match prediction submitted: matchId={} userId={}", matchId, userId);
        return matchPredictionRepository.save(prediction);
    }

    // ─── US-018: View my match prediction ────────────────────────────────────

    @Transactional(readOnly = true)
    public Optional<MatchPrediction> getMyMatchPrediction(UUID matchId, UUID userId) {
        matchService.findById(matchId); // validate match exists
        return matchPredictionRepository.findByMatchIdAndUserId(matchId, userId);
    }

    // ─── US-019 / US-020: All predictions (after lock) with result enrichment ─

    @Transactional(readOnly = true)
    public Page<MatchPrediction> getAllMatchPredictions(UUID matchId, Pageable pageable) {
        Match match = matchService.findById(matchId);

        if (match.getStatus() == MatchStatus.SCHEDULED && Instant.now().isBefore(match.getLockTime())) {
            throw new PredictionWindowOpenException(matchId);
        }

        return matchPredictionRepository.findByMatchId(matchId, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<MatchResult> getMatchResult(UUID matchId) {
        return matchResultRepository.findByMatchId(matchId);
    }

    // ─── US-021: Submit league prediction ───────────────────────────────────

    public List<LeaguePrediction> submitLeaguePrediction(UUID seasonId, UUID userId,
                                                          List<SubmitLeaguePredictionRequest.Entry> entries) {
        Season season = seasonService.findById(seasonId);
        User user = userService.findById(userId);

        // Lock enforcement: window closes at leagueLockTime
        if (season.getLeagueLockTime() != null && !Instant.now().isBefore(season.getLeagueLockTime())) {
            throw new PredictionLockedException(seasonId);
        }

        // All enrolled teams must be included
        List<UUID> enrolledTeamIds = seasonTeamRepository.findBySeasonId(seasonId)
                .stream().map(st -> st.getTeam().getId()).collect(Collectors.toList());
        int teamCount = enrolledTeamIds.size();

        Set<UUID> submittedTeamIds = entries.stream()
                .map(SubmitLeaguePredictionRequest.Entry::teamId)
                .collect(Collectors.toSet());

        if (submittedTeamIds.size() != teamCount || !submittedTeamIds.containsAll(enrolledTeamIds)) {
            throw new InvalidLeaguePredictionException(
                    "prediction must include all " + teamCount + " enrolled teams");
        }

        // Positions must be unique and in range 1..N
        Set<Integer> positions = new HashSet<>();
        for (SubmitLeaguePredictionRequest.Entry entry : entries) {
            int pos = entry.predictedPosition();
            if (pos < 1 || pos > teamCount) {
                throw new InvalidLeaguePredictionException(
                        "position " + pos + " out of range 1.." + teamCount);
            }
            if (!positions.add(pos)) {
                throw new InvalidLeaguePredictionException("duplicate position " + pos);
            }
        }

        // All submitted teams must be enrolled
        for (UUID teamId : submittedTeamIds) {
            if (!seasonTeamRepository.existsBySeasonIdAndTeamId(seasonId, teamId)) {
                throw new InvalidLeaguePredictionException("team " + teamId + " is not enrolled in this season");
            }
        }

        leaguePredictionRepository.deleteAll(
                leaguePredictionRepository.findBySeasonIdAndUserId(seasonId, userId));

        List<LeaguePrediction> saved = entries.stream().map(entry -> {
            Team team = teamRepository.findById(entry.teamId())
                    .orElseThrow(() -> new TeamNotFoundException(entry.teamId()));
            return leaguePredictionRepository.save(
                    new LeaguePrediction(season, user, team, entry.predictedPosition()));
        }).toList();
        log.info("League prediction submitted: seasonId={} userId={} teams={}", seasonId, userId, teamCount);
        return saved;
    }

    // ─── US-022: View league predictions ────────────────────────────────────

    @Transactional(readOnly = true)
    public List<LeaguePrediction> getMyLeaguePrediction(UUID seasonId, UUID userId) {
        return leaguePredictionRepository.findBySeasonIdAndUserId(seasonId, userId);
    }

    @Transactional(readOnly = true)
    public Page<LeaguePrediction> getAllLeaguePredictions(UUID seasonId, Pageable pageable) {
        Season season = seasonService.findById(seasonId);

        // Only visible after prediction window closes (leagueLockTime has passed)
        if (season.getLeagueLockTime() == null || Instant.now().isBefore(season.getLeagueLockTime())) {
            throw new PredictionWindowOpenException(seasonId);
        }

        return leaguePredictionRepository.findBySeasonId(seasonId, pageable);
    }

    // ─── US-020: Head-to-head comparison ────────────────────────────────────

    @Transactional(readOnly = true)
    public HeadToHead getHeadToHead(UUID matchId, UUID userId, UUID opponentId) {
        Match match = matchService.findById(matchId);

        if (match.getStatus() == MatchStatus.SCHEDULED && Instant.now().isBefore(match.getLockTime())) {
            throw new PredictionWindowOpenException(matchId);
        }

        MatchPrediction mine = matchPredictionRepository.findByMatchIdAndUserId(matchId, userId).orElse(null);
        MatchPrediction theirs = matchPredictionRepository.findByMatchIdAndUserId(matchId, opponentId).orElse(null);
        Optional<MatchResult> result = matchResultRepository.findByMatchId(matchId);

        return new HeadToHead(mine, theirs, result);
    }

    public record HeadToHead(MatchPrediction mine, MatchPrediction theirs, Optional<MatchResult> result) {}
}
