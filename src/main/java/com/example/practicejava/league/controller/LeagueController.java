package com.example.practicejava.league.controller;

import com.example.practicejava.common.ApiResponse;
import com.example.practicejava.league.League;
import com.example.practicejava.league.dto.CreateLeagueRequest;
import com.example.practicejava.league.dto.LeagueResponse;
import com.example.practicejava.league.dto.UpdateLeagueRequest;
import com.example.practicejava.league.service.LeagueService;
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
@RequestMapping("/api/v1/leagues")
public class LeagueController {

    private final LeagueService leagueService;

    public LeagueController(LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LeagueResponse>>> list(HttpServletRequest req) {
        List<LeagueResponse> leagues = leagueService.findAll().stream().map(LeagueResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(leagues, req.getRequestURI()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeagueResponse>> get(@PathVariable UUID id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(LeagueResponse.from(leagueService.findById(id)), req.getRequestURI()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LeagueResponse>> create(@Valid @RequestBody CreateLeagueRequest request,
                                                               HttpServletRequest req) {
        League created = leagueService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("League created successfully", LeagueResponse.from(created), req.getRequestURI()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeagueResponse>> update(@PathVariable UUID id,
                                                               @Valid @RequestBody UpdateLeagueRequest request,
                                                               HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(LeagueResponse.from(leagueService.update(id, request)), req.getRequestURI()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id, HttpServletRequest req) {
        leagueService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted(req.getRequestURI()));
    }
}
