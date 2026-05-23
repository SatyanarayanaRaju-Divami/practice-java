package com.example.practicejava.prediction.service;

import com.example.practicejava.league.Season;
import com.example.practicejava.league.SeasonStatus;
import com.example.practicejava.league.service.SeasonService;
import com.example.practicejava.match.repository.MatchResultRepository;
import com.example.practicejava.match.service.MatchService;
import com.example.practicejava.player.repository.PlayerRepository;
import com.example.practicejava.prediction.InvalidLeaguePredictionException;
import com.example.practicejava.prediction.PredictionLockedException;
import com.example.practicejava.prediction.dto.SubmitLeaguePredictionRequest;
import com.example.practicejava.prediction.repository.LeaguePredictionRepository;
import com.example.practicejava.prediction.repository.MatchPredictionRepository;
import com.example.practicejava.team.SeasonTeam;
import com.example.practicejava.team.Team;
import com.example.practicejava.team.repository.SeasonTeamRepository;
import com.example.practicejava.team.repository.TeamRepository;
import com.example.practicejava.user.User;
import com.example.practicejava.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PredictionServiceTest {

    @Mock LeaguePredictionRepository leaguePredictionRepository;
    @Mock MatchPredictionRepository matchPredictionRepository;
    @Mock SeasonService seasonService;
    @Mock MatchService matchService;
    @Mock MatchResultRepository matchResultRepository;
    @Mock UserService userService;
    @Mock TeamRepository teamRepository;
    @Mock PlayerRepository playerRepository;
    @Mock SeasonTeamRepository seasonTeamRepository;

    @InjectMocks PredictionService predictionService;

    private final UUID seasonId = UUID.randomUUID();
    private final UUID userId   = UUID.randomUUID();
    private Season season;
    private User   user;

    @BeforeEach
    void setUp() {
        season = mock(Season.class);
        user   = mock(User.class);
        when(seasonService.findById(seasonId)).thenReturn(season);
        when(userService.findById(userId)).thenReturn(user);
    }

    // ── submitLeaguePrediction ────────────────────────────────────────────────

    @Test
    void submitLeague_throwsWhenWindowClosed() {
        when(season.getLeagueLockTime()).thenReturn(Instant.now().minus(1, ChronoUnit.HOURS));

        List<SubmitLeaguePredictionRequest.Entry> entries = List.of(
                new SubmitLeaguePredictionRequest.Entry(UUID.randomUUID(), 1));

        assertThatThrownBy(() -> predictionService.submitLeaguePrediction(seasonId, userId, entries))
                .isInstanceOf(PredictionLockedException.class);
    }

    @Test
    void submitLeague_throwsWhenNotAllTeamsIncluded() {
        when(season.getLeagueLockTime()).thenReturn(Instant.now().plus(1, ChronoUnit.HOURS));

        UUID team1Id = UUID.randomUUID();
        UUID team2Id = UUID.randomUUID();
        Team team1 = mock(Team.class); when(team1.getId()).thenReturn(team1Id);
        Team team2 = mock(Team.class); when(team2.getId()).thenReturn(team2Id);
        SeasonTeam st1 = mock(SeasonTeam.class); when(st1.getTeam()).thenReturn(team1);
        SeasonTeam st2 = mock(SeasonTeam.class); when(st2.getTeam()).thenReturn(team2);

        when(seasonTeamRepository.findBySeasonId(seasonId)).thenReturn(List.of(st1, st2));

        // Only submit 1 team instead of 2
        List<SubmitLeaguePredictionRequest.Entry> entries = List.of(
                new SubmitLeaguePredictionRequest.Entry(team1Id, 1));

        assertThatThrownBy(() -> predictionService.submitLeaguePrediction(seasonId, userId, entries))
                .isInstanceOf(InvalidLeaguePredictionException.class)
                .hasMessageContaining("2 enrolled teams");
    }

    @Test
    void submitLeague_throwsWhenDuplicatePositions() {
        when(season.getLeagueLockTime()).thenReturn(Instant.now().plus(1, ChronoUnit.HOURS));

        UUID team1Id = UUID.randomUUID();
        UUID team2Id = UUID.randomUUID();
        Team team1 = mock(Team.class); when(team1.getId()).thenReturn(team1Id);
        Team team2 = mock(Team.class); when(team2.getId()).thenReturn(team2Id);
        SeasonTeam st1 = mock(SeasonTeam.class); when(st1.getTeam()).thenReturn(team1);
        SeasonTeam st2 = mock(SeasonTeam.class); when(st2.getTeam()).thenReturn(team2);

        when(seasonTeamRepository.findBySeasonId(seasonId)).thenReturn(List.of(st1, st2));

        // Both teams get position 1 — duplicate
        List<SubmitLeaguePredictionRequest.Entry> entries = List.of(
                new SubmitLeaguePredictionRequest.Entry(team1Id, 1),
                new SubmitLeaguePredictionRequest.Entry(team2Id, 1));

        assertThatThrownBy(() -> predictionService.submitLeaguePrediction(seasonId, userId, entries))
                .isInstanceOf(InvalidLeaguePredictionException.class)
                .hasMessageContaining("duplicate position 1");
    }

    @Test
    void submitLeague_throwsWhenPositionOutOfRange() {
        when(season.getLeagueLockTime()).thenReturn(Instant.now().plus(1, ChronoUnit.HOURS));

        UUID team1Id = UUID.randomUUID();
        UUID team2Id = UUID.randomUUID();
        Team team1 = mock(Team.class); when(team1.getId()).thenReturn(team1Id);
        Team team2 = mock(Team.class); when(team2.getId()).thenReturn(team2Id);
        SeasonTeam st1 = mock(SeasonTeam.class); when(st1.getTeam()).thenReturn(team1);
        SeasonTeam st2 = mock(SeasonTeam.class); when(st2.getTeam()).thenReturn(team2);

        when(seasonTeamRepository.findBySeasonId(seasonId)).thenReturn(List.of(st1, st2));

        // Position 3 is out of range for 2-team season
        List<SubmitLeaguePredictionRequest.Entry> entries = List.of(
                new SubmitLeaguePredictionRequest.Entry(team1Id, 1),
                new SubmitLeaguePredictionRequest.Entry(team2Id, 3));

        assertThatThrownBy(() -> predictionService.submitLeaguePrediction(seasonId, userId, entries))
                .isInstanceOf(InvalidLeaguePredictionException.class)
                .hasMessageContaining("out of range");
    }

    // ── getAllLeaguePredictions ────────────────────────────────────────────────

    @Test
    void getAllLeague_throwsWhenWindowStillOpen() {
        when(season.getLeagueLockTime()).thenReturn(Instant.now().plus(1, ChronoUnit.HOURS));

        assertThatThrownBy(() -> predictionService.getAllLeaguePredictions(seasonId, org.springframework.data.domain.Pageable.unpaged()))
                .isInstanceOf(com.example.practicejava.prediction.PredictionWindowOpenException.class);
    }

    @Test
    void getAllLeague_returnsResultsAfterWindowClose() {
        when(season.getLeagueLockTime()).thenReturn(Instant.now().minus(1, ChronoUnit.HOURS));
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.Pageable.unpaged();
        when(leaguePredictionRepository.findBySeasonId(seasonId, pageable))
                .thenReturn(org.springframework.data.domain.Page.empty());

        var result = predictionService.getAllLeaguePredictions(seasonId, pageable);

        org.assertj.core.api.Assertions.assertThat(result.getContent()).isEmpty();
    }
}
