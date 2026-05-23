package com.example.practicejava.league.controller;

import com.example.practicejava.common.ApiResponse;
import com.example.practicejava.league.Season;
import com.example.practicejava.league.dto.CreateSeasonRequest;
import com.example.practicejava.league.dto.PublishFinalStandingsRequest;
import com.example.practicejava.league.dto.SeasonResponse;
import com.example.practicejava.league.service.SeasonService;
import com.example.practicejava.match.service.ResultService;
import com.example.practicejava.team.dto.EnrollTeamRequest;
import com.example.practicejava.team.dto.SeasonTeamResponse;
import com.example.practicejava.team.service.SeasonTeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Seasons", description = "Manage seasons, team enrollment, and lifecycle transitions")
@RestController
@RequestMapping("/api/v1")
public class SeasonController {

    private final SeasonService seasonService;
    private final SeasonTeamService seasonTeamService;
    private final ResultService resultService;

    public SeasonController(SeasonService seasonService, SeasonTeamService seasonTeamService,
                            ResultService resultService) {
        this.seasonService = seasonService;
        this.seasonTeamService = seasonTeamService;
        this.resultService = resultService;
    }

    @Operation(summary = "List seasons for a league (paginated)")
    @GetMapping("/leagues/{leagueId}/seasons")
    public ResponseEntity<ApiResponse<Page<SeasonResponse>>> listByLeague(
            @PathVariable UUID leagueId,
            @PageableDefault(size = 20, sort = "name") Pageable pageable,
            HttpServletRequest req) {
        Page<SeasonResponse> seasons = seasonService.findByLeagueId(leagueId, pageable).map(SeasonResponse::from);
        return ResponseEntity.ok(ApiResponse.ok(seasons, req.getRequestURI()));
    }

    @Operation(summary = "Create a new season under a league (admin only)")
    @PostMapping("/leagues/{leagueId}/seasons")
    public ResponseEntity<ApiResponse<SeasonResponse>> create(@PathVariable UUID leagueId,
                                                               @Valid @RequestBody CreateSeasonRequest request,
                                                               HttpServletRequest req) {
        Season created = seasonService.create(leagueId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Season created successfully", SeasonResponse.from(created), req.getRequestURI()));
    }

    @Operation(summary = "Get a season by ID")
    @GetMapping("/seasons/{id}")
    public ResponseEntity<ApiResponse<SeasonResponse>> get(@PathVariable UUID id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(SeasonResponse.from(seasonService.findById(id)), req.getRequestURI()));
    }

    @Operation(summary = "Activate a season (DRAFT → OPEN) (admin only)")
    @PutMapping("/seasons/{id}/activate")
    public ResponseEntity<ApiResponse<SeasonResponse>> activate(@PathVariable UUID id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Season activated", SeasonResponse.from(seasonService.activate(id)), req.getRequestURI()));
    }

    @Operation(summary = "Close a season (COMPLETED → CLOSED) (admin only)")
    @PutMapping("/seasons/{id}/close")
    public ResponseEntity<ApiResponse<SeasonResponse>> close(@PathVariable UUID id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Season closed", SeasonResponse.from(seasonService.close(id)), req.getRequestURI()));
    }

    @Operation(summary = "Delete a season (admin only)")
    @DeleteMapping("/seasons/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id,
                                                     @AuthenticationPrincipal UUID deletedBy,
                                                     HttpServletRequest req) {
        seasonService.delete(id, deletedBy);
        return ResponseEntity.ok(ApiResponse.deleted(req.getRequestURI()));
    }

    // Season-Team enrollment

    @Operation(summary = "List teams enrolled in a season")
    @GetMapping("/seasons/{id}/teams")
    public ResponseEntity<ApiResponse<List<SeasonTeamResponse>>> listTeams(@PathVariable UUID id,
                                                                             HttpServletRequest req) {
        List<SeasonTeamResponse> teams = seasonTeamService.findBySeasonId(id).stream()
                .map(SeasonTeamResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(teams, req.getRequestURI()));
    }

    @Operation(summary = "Enroll a team in a season (admin only)")
    @PostMapping("/seasons/{id}/teams")
    public ResponseEntity<ApiResponse<SeasonTeamResponse>> enrollTeam(@PathVariable UUID id,
                                                                        @Valid @RequestBody EnrollTeamRequest request,
                                                                        HttpServletRequest req) {
        SeasonTeamResponse enrolled = SeasonTeamResponse.from(seasonTeamService.enroll(id, request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Team enrolled successfully", enrolled, req.getRequestURI()));
    }

    @Operation(summary = "Remove a team from a season (admin only)")
    @DeleteMapping("/seasons/{seasonId}/teams/{teamId}")
    public ResponseEntity<ApiResponse<Void>> unenrollTeam(@PathVariable UUID seasonId,
                                                           @PathVariable UUID teamId,
                                                           @AuthenticationPrincipal UUID deletedBy,
                                                           HttpServletRequest req) {
        seasonTeamService.unenroll(seasonId, teamId, deletedBy);
        return ResponseEntity.ok(ApiResponse.deleted(req.getRequestURI()));
    }

    // ─── US-025: Publish final league standings (admin) ──────────────────────

    @Operation(summary = "Publish final league standings (admin only)")
    @PostMapping("/seasons/{id}/publish-result")
    public ResponseEntity<ApiResponse<Void>> publishFinalStandings(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID publishedBy,
            @Valid @RequestBody PublishFinalStandingsRequest request,
            HttpServletRequest req) {
        resultService.publishFinalStandings(id, publishedBy, request);
        return ResponseEntity.ok(ApiResponse.ok("Final standings published", null, req.getRequestURI()));
    }
}
