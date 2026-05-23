package com.example.practicejava.league.service;

import com.example.practicejava.appconfig.AppConfigEntity;
import com.example.practicejava.appconfig.service.AppConfigService;
import com.example.practicejava.league.League;
import com.example.practicejava.league.Season;
import com.example.practicejava.league.SeasonNotActivatableException;
import com.example.practicejava.league.SeasonNotClosableException;
import com.example.practicejava.league.SeasonStatus;
import com.example.practicejava.league.repository.SeasonRepository;
import com.example.practicejava.match.repository.MatchRepository;
import com.example.practicejava.team.repository.SeasonTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeasonServiceTest {

    @Mock SeasonRepository seasonRepository;
    @Mock LeagueService leagueService;
    @Mock SeasonTeamRepository seasonTeamRepository;
    @Mock MatchRepository matchRepository;
    @Mock AppConfigService appConfigService;

    @InjectMocks SeasonService seasonService;

    private Season season;
    private final UUID seasonId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        League league = new League("IPL", "Indian Premier League");
        season = new Season(league, "Season 1");
    }

    // ── activate ─────────────────────────────────────────────────────────────

    @Test
    void activate_throwsWhenStatusIsNotDraft() {
        season.setStatus(SeasonStatus.OPEN);
        when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(season));

        assertThatThrownBy(() -> seasonService.activate(seasonId))
                .isInstanceOf(SeasonNotActivatableException.class)
                .hasMessageContaining("DRAFT");
    }

    @Test
    void activate_throwsWhenNoTeamsEnrolled() {
        when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(season));
        when(seasonTeamRepository.countBySeasonId(seasonId)).thenReturn(0);

        assertThatThrownBy(() -> seasonService.activate(seasonId))
                .isInstanceOf(SeasonNotActivatableException.class)
                .hasMessageContaining("no enrolled teams");
    }

    @Test
    void activate_throwsWhenNoMatchesScheduled() {
        when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(season));
        when(seasonTeamRepository.countBySeasonId(seasonId)).thenReturn(4);
        when(matchRepository.countBySeasonId(seasonId)).thenReturn(0);

        assertThatThrownBy(() -> seasonService.activate(seasonId))
                .isInstanceOf(SeasonNotActivatableException.class)
                .hasMessageContaining("no scheduled matches");
    }

    @Test
    void activate_throwsWhenFirstMatchStartTimeIsNull() {
        when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(season));
        when(seasonTeamRepository.countBySeasonId(seasonId)).thenReturn(4);
        when(matchRepository.countBySeasonId(seasonId)).thenReturn(10);
        // firstMatchStartTime is null by default

        assertThatThrownBy(() -> seasonService.activate(seasonId))
                .isInstanceOf(SeasonNotActivatableException.class)
                .hasMessageContaining("first match start time");
    }

    @Test
    void activate_calculatesLeagueLockTimeAndSetsStatusOpen() {
        Instant firstMatch = Instant.now().plus(10, ChronoUnit.DAYS);
        season.setFirstMatchStartTime(firstMatch);

        when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(season));
        when(seasonTeamRepository.countBySeasonId(seasonId)).thenReturn(4);
        when(matchRepository.countBySeasonId(seasonId)).thenReturn(10);
        when(appConfigService.findByKey("league.lock.offset.hours"))
                .thenReturn(new AppConfigEntity("league.lock.offset.hours", "4", "desc"));

        Season result = seasonService.activate(seasonId);

        assertThat(result.getStatus()).isEqualTo(SeasonStatus.OPEN);
        assertThat(result.getLeagueLockTime())
                .isEqualTo(firstMatch.minus(4, ChronoUnit.HOURS));
    }

    // ── close ─────────────────────────────────────────────────────────────────

    @Test
    void close_throwsWhenStatusIsNotCompleted() {
        season.setStatus(SeasonStatus.OPEN);
        when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(season));

        assertThatThrownBy(() -> seasonService.close(seasonId))
                .isInstanceOf(SeasonNotClosableException.class);
    }

    @Test
    void close_transitionsToClosedWhenCompleted() {
        season.setStatus(SeasonStatus.COMPLETED);
        when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(season));

        Season result = seasonService.close(seasonId);

        assertThat(result.getStatus()).isEqualTo(SeasonStatus.CLOSED);
    }
}
