package com.example.practicejava.team.controller;

import com.example.practicejava.common.ApiResponse;
import com.example.practicejava.team.Team;
import com.example.practicejava.team.dto.CreateTeamRequest;
import com.example.practicejava.team.dto.TeamResponse;
import com.example.practicejava.team.dto.UpdateTeamRequest;
import com.example.practicejava.team.service.TeamService;
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

import java.util.UUID;

@Tag(name = "Teams", description = "Manage teams")
@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @Operation(summary = "List all teams (paginated)")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TeamResponse>>> list(
            @PageableDefault(size = 20, sort = "name") Pageable pageable,
            HttpServletRequest req) {
        Page<TeamResponse> teams = teamService.findAll(pageable).map(TeamResponse::from);
        return ResponseEntity.ok(ApiResponse.ok(teams, req.getRequestURI()));
    }

    @Operation(summary = "Get a team by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeamResponse>> get(@PathVariable UUID id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(TeamResponse.from(teamService.findById(id)), req.getRequestURI()));
    }

    @Operation(summary = "Create a new team (admin only)")
    @PostMapping
    public ResponseEntity<ApiResponse<TeamResponse>> create(@Valid @RequestBody CreateTeamRequest request,
                                                             HttpServletRequest req) {
        Team created = teamService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Team created successfully", TeamResponse.from(created), req.getRequestURI()));
    }

    @Operation(summary = "Update a team (admin only)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TeamResponse>> update(@PathVariable UUID id,
                                                             @Valid @RequestBody UpdateTeamRequest request,
                                                             HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(TeamResponse.from(teamService.update(id, request)), req.getRequestURI()));
    }

    @Operation(summary = "Delete a team (admin only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id,
                                                     @AuthenticationPrincipal UUID deletedBy,
                                                     HttpServletRequest req) {
        teamService.delete(id, deletedBy);
        return ResponseEntity.ok(ApiResponse.deleted(req.getRequestURI()));
    }
}
