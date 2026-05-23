package com.example.practicejava.team.controller;

import com.example.practicejava.common.ApiResponse;
import com.example.practicejava.team.Team;
import com.example.practicejava.team.dto.CreateTeamRequest;
import com.example.practicejava.team.dto.TeamResponse;
import com.example.practicejava.team.dto.UpdateTeamRequest;
import com.example.practicejava.team.service.TeamService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TeamResponse>>> list(HttpServletRequest req) {
        List<TeamResponse> teams = teamService.findAll().stream().map(TeamResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(teams, req.getRequestURI()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeamResponse>> get(@PathVariable UUID id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(TeamResponse.from(teamService.findById(id)), req.getRequestURI()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TeamResponse>> create(@Valid @RequestBody CreateTeamRequest request,
                                                             HttpServletRequest req) {
        Team created = teamService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Team created successfully", TeamResponse.from(created), req.getRequestURI()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TeamResponse>> update(@PathVariable UUID id,
                                                             @Valid @RequestBody UpdateTeamRequest request,
                                                             HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(TeamResponse.from(teamService.update(id, request)), req.getRequestURI()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id, HttpServletRequest req) {
        teamService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted(req.getRequestURI()));
    }
}
