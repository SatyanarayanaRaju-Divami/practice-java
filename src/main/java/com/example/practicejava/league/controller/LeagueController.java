package com.example.practicejava.league.controller;

import com.example.practicejava.common.ApiResponse;
import com.example.practicejava.league.League;
import com.example.practicejava.league.dto.CreateLeagueRequest;
import com.example.practicejava.league.dto.LeagueResponse;
import com.example.practicejava.league.dto.UpdateLeagueRequest;
import com.example.practicejava.league.service.LeagueService;
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

@Tag(name = "Leagues", description = "Manage leagues")
@RestController
@RequestMapping("/api/v1/leagues")
public class LeagueController {

    private final LeagueService leagueService;

    public LeagueController(LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    @Operation(summary = "List all leagues (paginated)")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<LeagueResponse>>> list(
            @PageableDefault(size = 20, sort = "name") Pageable pageable,
            HttpServletRequest req) {
        Page<LeagueResponse> leagues = leagueService.findAll(pageable).map(LeagueResponse::from);
        return ResponseEntity.ok(ApiResponse.ok(leagues, req.getRequestURI()));
    }

    @Operation(summary = "Get a league by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeagueResponse>> get(@PathVariable UUID id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(LeagueResponse.from(leagueService.findById(id)), req.getRequestURI()));
    }

    @Operation(summary = "Create a new league (admin only)")
    @PostMapping
    public ResponseEntity<ApiResponse<LeagueResponse>> create(@Valid @RequestBody CreateLeagueRequest request,
                                                               HttpServletRequest req) {
        League created = leagueService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("League created successfully", LeagueResponse.from(created), req.getRequestURI()));
    }

    @Operation(summary = "Update a league (admin only)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeagueResponse>> update(@PathVariable UUID id,
                                                               @Valid @RequestBody UpdateLeagueRequest request,
                                                               HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(LeagueResponse.from(leagueService.update(id, request)), req.getRequestURI()));
    }

    @Operation(summary = "Delete a league (admin only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id,
                                                     @AuthenticationPrincipal UUID deletedBy,
                                                     HttpServletRequest req) {
        leagueService.delete(id, deletedBy);
        return ResponseEntity.ok(ApiResponse.deleted(req.getRequestURI()));
    }
}
