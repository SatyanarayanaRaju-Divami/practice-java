package com.example.practicejava.scoring.service;

import com.example.practicejava.match.Match;
import com.example.practicejava.match.MatchResult;
import com.example.practicejava.notification.service.NotificationService;
import com.example.practicejava.prediction.LeaguePrediction;
import com.example.practicejava.prediction.MatchPrediction;
import com.example.practicejava.prediction.repository.LeaguePredictionRepository;
import com.example.practicejava.prediction.repository.MatchPredictionRepository;
import com.example.practicejava.scoring.Leaderboard;
import com.example.practicejava.scoring.PredictionType;
import com.example.practicejava.scoring.Score;
import com.example.practicejava.scoring.ScoreBreakdown;
import com.example.practicejava.scoring.repository.LeaderboardRepository;
import com.example.practicejava.scoring.repository.ScoreBreakdownRepository;
import com.example.practicejava.scoring.repository.ScoreRepository;
import com.example.practicejava.standing.LeagueStanding;
import com.example.practicejava.standing.repository.LeagueStandingRepository;
import com.example.practicejava.user.UserRole;
import com.example.practicejava.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ScoreCalculationService {

    private static final Logger log = LoggerFactory.getLogger(ScoreCalculationService.class);

    private final MatchPredictionRepository matchPredictionRepository;
    private final LeaguePredictionRepository leaguePredictionRepository;
    private final ScoreRepository scoreRepository;
    private final ScoreBreakdownRepository scoreBreakdownRepository;
    private final LeaderboardRepository leaderboardRepository;
    private final LeagueStandingRepository leagueStandingRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public ScoreCalculationService(MatchPredictionRepository matchPredictionRepository,
                                   LeaguePredictionRepository leaguePredictionRepository,
                                   ScoreRepository scoreRepository,
                                   ScoreBreakdownRepository scoreBreakdownRepository,
                                   LeaderboardRepository leaderboardRepository,
                                   LeagueStandingRepository leagueStandingRepository,
                                   NotificationService notificationService,
                                   UserRepository userRepository) {
        this.matchPredictionRepository = matchPredictionRepository;
        this.leaguePredictionRepository = leaguePredictionRepository;
        this.scoreRepository = scoreRepository;
        this.scoreBreakdownRepository = scoreBreakdownRepository;
        this.leaderboardRepository = leaderboardRepository;
        this.leagueStandingRepository = leagueStandingRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @Async("scoreExecutor")
    @Transactional
    public void calculateMatchScores(MatchResult result) {
        Match match = result.getMatch();
        UUID seasonId = match.getSeason().getId();

        List<MatchPrediction> predictions = matchPredictionRepository.findByMatchId(match.getId());

        for (MatchPrediction prediction : predictions) {
            Score score = scoreRepository
                    .findBySeasonIdAndUserId(seasonId, prediction.getUser().getId())
                    .orElseGet(() -> scoreRepository.save(new Score(match.getSeason(), prediction.getUser())));

            scoreWinner(score, match, prediction, result);
            scoreToss(score, match, prediction, result);
            scorePotm(score, match, prediction, result);
        }

        int usersUpdated = rebuildLeaderboard(seasonId);

        log.info("Match scores calculated: matchId={} predictors={}", match.getId(), predictions.size());
        // US-030: score update email to each predictor
        List<UUID> predictorIds = predictions.stream()
                .map(p -> p.getUser().getId()).toList();
        notificationService.sendScoreUpdateEmails(result, predictorIds);

        // US-031: leaderboard recap email to all admins
        notificationService.sendLeaderboardRecapToAdmins(
                userRepository.findByRole(UserRole.ADMIN),
                match.getSeason().getName(),
                match,
                usersUpdated);
    }

    @Async("scoreExecutor")
    @Transactional
    public void calculateLeagueScores(UUID seasonId,
                                      Map<UUID, Integer> finalPositions) {
        List<LeaguePrediction> allPredictions = leaguePredictionRepository.findBySeasonId(seasonId);

        // group by userId
        Map<UUID, List<LeaguePrediction>> byUser = allPredictions.stream()
                .collect(Collectors.groupingBy(lp -> lp.getUser().getId()));

        for (Map.Entry<UUID, List<LeaguePrediction>> entry : byUser.entrySet()) {
            UUID userId = entry.getKey();
            List<LeaguePrediction> userPredictions = entry.getValue();

            if (userPredictions.isEmpty()) continue;

            Score score = scoreRepository
                    .findBySeasonIdAndUserId(seasonId, userId)
                    .orElseGet(() -> {
                        LeaguePrediction first = userPredictions.get(0);
                        return scoreRepository.save(new Score(first.getSeason(), first.getUser()));
                    });

            for (LeaguePrediction lp : userPredictions) {
                Integer actualPosition = finalPositions.get(lp.getTeam().getId());
                if (actualPosition != null && actualPosition == lp.getPredictedPosition()) {
                    score.addPoints(1);
                    scoreBreakdownRepository.save(
                            new ScoreBreakdown(score, null, PredictionType.LEAGUE_STANDING, 1));
                }
            }
        }

        rebuildLeaderboard(seasonId); // league scores don't trigger notifications
        log.info("League scores calculated: seasonId={} users={}", seasonId, byUser.size());
    }

    private void scoreWinner(Score score, Match match, MatchPrediction prediction, MatchResult result) {
        boolean correct;
        if (result.isDraw()) {
            correct = prediction.getPredictedWinnerTeam() == null;
        } else if (result.getWinnerTeam() != null && prediction.getPredictedWinnerTeam() != null) {
            correct = result.getWinnerTeam().getId().equals(prediction.getPredictedWinnerTeam().getId());
        } else {
            correct = false;
        }
        if (correct) {
            score.addPoints(1);
            scoreBreakdownRepository.save(new ScoreBreakdown(score, match, PredictionType.MATCH_WINNER, 1));
        }
    }

    private void scoreToss(Score score, Match match, MatchPrediction prediction, MatchResult result) {
        boolean correct = result.getTossWinnerTeam() != null
                && prediction.getPredictedTossWinner() != null
                && result.getTossWinnerTeam().getId().equals(prediction.getPredictedTossWinner().getId());
        if (correct) {
            score.addPoints(1);
            scoreBreakdownRepository.save(new ScoreBreakdown(score, match, PredictionType.TOSS, 1));
        }
    }

    private void scorePotm(Score score, Match match, MatchPrediction prediction, MatchResult result) {
        boolean correct = result.getPlayerOfMatch() != null
                && prediction.getPredictedPotmPlayer() != null
                && result.getPlayerOfMatch().getId().equals(prediction.getPredictedPotmPlayer().getId());
        if (correct) {
            score.addPoints(1);
            scoreBreakdownRepository.save(new ScoreBreakdown(score, match, PredictionType.POTM, 1));
        }
    }

    private int rebuildLeaderboard(UUID seasonId) {
        List<Score> scores = scoreRepository.findBySeasonId(seasonId);

        List<Score> ranked = scores.stream()
                .sorted(Comparator.comparingInt(Score::getTotalPoints).reversed()
                        .thenComparing(s -> s.getUpdatedAt(), Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(s -> s.getUser().getDisplayName()))
                .toList();

        for (int i = 0; i < ranked.size(); i++) {
            Score s = ranked.get(i);
            Leaderboard entry = leaderboardRepository
                    .findBySeasonIdAndUserId(seasonId, s.getUser().getId())
                    .orElseGet(() -> leaderboardRepository.save(new Leaderboard(s.getSeason(), s.getUser())));
            entry.setRank(i + 1);
            entry.setTotalPoints(s.getTotalPoints());
            entry.setLastCalculatedAt(Instant.now());
        }

        return ranked.size();
    }
}
