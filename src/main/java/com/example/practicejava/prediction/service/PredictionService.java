package com.example.practicejava.prediction.service;

import com.example.practicejava.league.Season;
import com.example.practicejava.league.SeasonNotFoundException;
import com.example.practicejava.league.repository.SeasonRepository;
import com.example.practicejava.match.Match;
import com.example.practicejava.match.MatchNotFoundException;
import com.example.practicejava.match.repository.MatchRepository;
import com.example.practicejava.prediction.LeaguePrediction;
import com.example.practicejava.prediction.MatchPrediction;
import com.example.practicejava.prediction.dto.SubmitLeaguePredictionRequest;
import com.example.practicejava.prediction.dto.SubmitMatchPredictionRequest;
import com.example.practicejava.prediction.repository.LeaguePredictionRepository;
import com.example.practicejava.prediction.repository.MatchPredictionRepository;
import com.example.practicejava.player.Player;
import com.example.practicejava.player.repository.PlayerRepository;
import com.example.practicejava.team.Team;
import com.example.practicejava.team.repository.TeamRepository;
import com.example.practicejava.user.User;
import com.example.practicejava.user.UserNotFoundException;
import com.example.practicejava.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PredictionService {

    private final LeaguePredictionRepository leaguePredictionRepository;
    private final MatchPredictionRepository matchPredictionRepository;
    private final SeasonRepository seasonRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public PredictionService(LeaguePredictionRepository leaguePredictionRepository,
                             MatchPredictionRepository matchPredictionRepository,
                             SeasonRepository seasonRepository,
                             MatchRepository matchRepository,
                             UserRepository userRepository,
                             TeamRepository teamRepository,
                             PlayerRepository playerRepository) {
        this.leaguePredictionRepository = leaguePredictionRepository;
        this.matchPredictionRepository = matchPredictionRepository;
        this.seasonRepository = seasonRepository;
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    public List<LeaguePrediction> submitLeaguePrediction(UUID seasonId, UUID userId,
                                                          List<SubmitLeaguePredictionRequest.Entry> entries) {
        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new SeasonNotFoundException(seasonId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Delete existing predictions for this user+season before re-submitting
        List<LeaguePrediction> existing = leaguePredictionRepository.findBySeasonIdAndUserId(seasonId, userId);
        leaguePredictionRepository.deleteAll(existing);

        return entries.stream().map(entry -> {
            Team team = teamRepository.findById(entry.teamId())
                    .orElseThrow(() -> new RuntimeException("Team not found: " + entry.teamId()));
            return leaguePredictionRepository.save(new LeaguePrediction(season, user, team, entry.predictedPosition()));
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<LeaguePrediction> getMyLeaguePrediction(UUID seasonId, UUID userId) {
        return leaguePredictionRepository.findBySeasonIdAndUserId(seasonId, userId);
    }

    @Transactional(readOnly = true)
    public List<LeaguePrediction> getAllLeaguePredictions(UUID seasonId) {
        return leaguePredictionRepository.findBySeasonId(seasonId);
    }

    public MatchPrediction submitMatchPrediction(UUID matchId, UUID userId,
                                                  SubmitMatchPredictionRequest request) {
        // TODO: enforce match lock — reject if match.status == LOCKED or COMPLETED
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException(matchId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        MatchPrediction prediction = matchPredictionRepository.findByMatchIdAndUserId(matchId, userId)
                .orElse(new MatchPrediction(match, user));

        if (request.predictedWinnerTeamId() != null) {
            Team winner = teamRepository.findById(request.predictedWinnerTeamId())
                    .orElseThrow(() -> new RuntimeException("Team not found: " + request.predictedWinnerTeamId()));
            prediction.setPredictedWinnerTeam(winner);
        }
        if (request.predictedTossWinnerId() != null) {
            Team tossWinner = teamRepository.findById(request.predictedTossWinnerId())
                    .orElseThrow(() -> new RuntimeException("Team not found: " + request.predictedTossWinnerId()));
            prediction.setPredictedTossWinner(tossWinner);
        }
        if (request.predictedPotmPlayerId() != null) {
            Player potm = playerRepository.findById(request.predictedPotmPlayerId())
                    .orElseThrow(() -> new RuntimeException("Player not found: " + request.predictedPotmPlayerId()));
            prediction.setPredictedPotmPlayer(potm);
        }

        return matchPredictionRepository.save(prediction);
    }

    @Transactional(readOnly = true)
    public MatchPrediction getMyMatchPrediction(UUID matchId, UUID userId) {
        return matchPredictionRepository.findByMatchIdAndUserId(matchId, userId)
                .orElseThrow(() -> new RuntimeException("Prediction not found for match " + matchId + " and user " + userId));
    }

    @Transactional(readOnly = true)
    public List<MatchPrediction> getAllMatchPredictions(UUID matchId) {
        return matchPredictionRepository.findByMatchId(matchId);
    }
}
